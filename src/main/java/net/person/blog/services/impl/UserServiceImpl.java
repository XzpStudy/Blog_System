package net.person.blog.services.impl;

import com.google.gson.Gson;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.BlogUserMapper;
import net.person.blog.dao.RefreshTokenMapper;
import net.person.blog.dao.SettingMapper;
import net.person.blog.pojo.BlogUser;
import net.person.blog.pojo.RefreshToken;
import net.person.blog.pojo.Setting;
import net.person.blog.response.ResponseResult;
import net.person.blog.response.ResponseState;
import net.person.blog.services.IUserService;
import net.person.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {


    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private BlogUserMapper blogUserMapper;

    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private Random random;

    @Autowired
    private TaskService taskService;

    @Autowired
    private Gson gson;

    private final static int[] captcha_font_type = {Captcha.FONT_1, Captcha.FONT_2, Captcha.FONT_3, Captcha.FONT_4,
            Captcha.FONT_5, Captcha.FONT_6, Captcha.FONT_7, Captcha.FONT_8, Captcha.FONT_9, Captcha.FONT_10};

    /**
     * 初始化管理员账号
     *
     * @param blogUser
     * @param request
     * @return
     */
    @Override
    public ResponseResult initManagerAccount(BlogUser blogUser, HttpServletRequest request) {
        //检查是否有初始化
        Setting managerAccountState = settingMapper.findOneByKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化");
        }
        //检查数据
        if (TextUtils.isEmpty(blogUser.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        if (TextUtils.isEmpty(blogUser.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtils.isEmpty(blogUser.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        //补充数据
        blogUser.setId(String.valueOf(idWorker.nextId()));
        blogUser.setRoles(Constants.User.ROLE_ADMIN);
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setState(Constants.User.DEFAULT_STATE);
        String remoteAddr = request.getRemoteAddr();
        blogUser.setLoginIp(remoteAddr);
        blogUser.setRegIp(remoteAddr);
        blogUser.setCreateTime(new Date());
        blogUser.setUpdateTime(new Date());
        //使用BCryptPasswordEncoder加密密码
        String password = blogUser.getPassword();
        String encode = passwordEncoder.encode(password);
        blogUser.setPassword(encode);
        //保存到数据库
        blogUserMapper.insertOneUser(blogUser);

        //添加标记
        Setting setting = new Setting();
        setting.setId(String.valueOf(idWorker.nextId()));
        setting.setKey(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        settingMapper.insertOneSetting(setting);
        return ResponseResult.SUCCESS("初始化成功");
    }

    /**
     * 生成随机的图灵验证码
     *
     * @param response
     * @param captchaKey
     */
    @Override
    public void createCaptcha(HttpServletResponse response, String captchaKey) throws IOException, FontFormatException {
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            return;
        }

        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        int captchaType = random.nextInt(3);
        Captcha targetCaptcha;
        if (captchaType == 0) {
            //普通图片类型
            targetCaptcha = new SpecCaptcha(130, 48, 5);
        } else if (captchaType == 1) {
            //Gif类型
            targetCaptcha = new GifCaptcha(130, 48);
        } else {
            //算数类型
            targetCaptcha = new ArithmeticCaptcha(130, 48);
            //设置俩个数的算数
            targetCaptcha.setLen(2);
        }
        int index = random.nextInt(captcha_font_type.length);
        //设置字体
        targetCaptcha.setFont(captcha_font_type[index]);
        //验证码字符类型
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        //保存到Redis中
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + key, content, 60 * 10 * 5);
        // 输出图片流
        targetCaptcha.out(response.getOutputStream());
    }

    /**
     * 发送邮箱验证码
     * 根据type来判断业务逻辑
     * 注册(register)：账号注册时的业务逻辑，如果已注册则提示已经注册
     * 修改密码(forget)：修改密码时的业务逻辑，个人认为不需要管，注册时已经保证了邮箱的唯一性
     * 更新邮箱(update)：更新邮箱的业务逻辑，个人认为也不需要管，注册时已经保证了邮箱的唯一性，只需要判断新邮箱唯一即可
     *
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public ResponseResult sendEmail(String type, HttpServletRequest request, String emailAddress) {
        //检查邮箱地址的格式是否正确
        boolean emailAddressOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!emailAddressOk) {
            return ResponseResult.FAILED("邮箱格式不正确");
        }
        if ("register".equals(type)) {
            BlogUser userEmail = blogUserMapper.findOneByEmail(emailAddress);
            if (userEmail != null) {
                return ResponseResult.FAILED("该邮箱已经注册过了");
            }
        }
        //防止暴力发送。根据IP地址和邮箱地址来限制发送邮件的次数。同一邮箱，间隔30秒一次，同一IP一小时最多10次
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null) {
            remoteAddr = remoteAddr.replace(":", "_");
        }
        log.info("sendEmail =====> ip =====>" + remoteAddr);
        Integer ipSendTime = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("验证码发送太过频繁");
        }
        Object hasEmailSend = redisUtil.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (hasEmailSend != null) {
            return ResponseResult.FAILED("验证码发送太过频繁");
        }
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        //生成验证码
        int code = random.nextInt(999999);
        log.info("sendEmail ====>  code ====>" + code);
        if (code < 100000) {
            code += 100000;
        }
        //发送邮件
        try {
            taskService.sendEmailVerifyCode(String.valueOf(code), emailAddress);
        } catch (MessagingException e) {
            return ResponseResult.FAILED("验证码发送失败，请稍后重试");
        }
        //将记录存储到redis中
        ipSendTime++;
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime, 60 * 60);
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        redisUtil.set(Constants.User.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 10 * 60);
        return ResponseResult.SUCCESS("验证码已发送成功，请注意查看您的邮箱");
    }

    /**
     * 实现用户注册
     *
     * @param blogUser
     * @return
     */
    @Override
    public ResponseResult register(BlogUser blogUser, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        //1.判断邮箱格式
        String email = blogUser.getEmail();
        boolean emailAddressOk = TextUtils.isEmailAddressOk(email);
        if (!emailAddressOk) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        //判断邮箱是否已经注册
        BlogUser userByEmail = blogUserMapper.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("邮箱已经注册");
        }
        //2.判断邮箱的验证码
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码不存在或已经失效");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确");
        }
        //3.判断图灵验证码
        String captchaVerifyCode = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("图灵验证码已失效");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("图灵验证码错误");
        }
        //4.判断用户名和密码
        String userName = blogUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("用户名不能为空");
        }
        String password = blogUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不能为空");
        }
        //5.加密密码
        blogUser.setPassword(passwordEncoder.encode(password));
        //6.补全其他信息
        //包含：id,注册ip，登录ip，角色，头像，状态，创建时间，更新时间
        String ipAddress = request.getRemoteAddr();
        blogUser.setId(idWorker.nextId() + "");
        blogUser.setRegIp(ipAddress);
        blogUser.setLoginIp(ipAddress);
        blogUser.setCreateTime(new Date());
        blogUser.setUpdateTime(new Date());
        blogUser.setAvatar(Constants.User.DEFAULT_AVATAR);
        blogUser.setRoles(Constants.User.ROLE_NORMAL);
        blogUser.setState(Constants.User.DEFAULT_STATE);
        blogUserMapper.insertOneUser(blogUser);
        //用户成功注册后删除Redis中的邮箱验证码和图灵验证码
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        return ResponseResult.GET(ResponseState.JOIN_IN_SUCCESS);
    }

    /**
     * 登录业务操作
     *
     * @param captcha
     * @param captchaKey
     * @param blogUser
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponseResult doLogin(String captcha, String captchaKey, BlogUser blogUser, HttpServletRequest request, HttpServletResponse response) {
        String captchaVerifyCode = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtils.isEmpty(captchaVerifyCode) || !captcha.equals(captchaVerifyCode)) {
            return ResponseResult.FAILED("图灵验证码错误");
        }
        //验证成功完后从redis中删除
        redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        String userName = blogUser.getUserName();
        if (TextUtils.isEmpty(userName)) {
            return ResponseResult.FAILED("账号不能为空");
        }
        String password = blogUser.getPassword();
        if (TextUtils.isEmpty(password)) {
            return ResponseResult.FAILED("密码不能为空");
        }

        //先进行昵称查询
        BlogUser userByUserName = blogUserMapper.findOneByUserName(userName);
        if (userByUserName == null) {
            //昵称为空时进行邮箱查询
            userByUserName = blogUserMapper.findOneByEmail(userName);
        }
        //邮箱为空就返回失败
        if (userByUserName == null) {
            return ResponseResult.FAILED("账号或密码错误");
        }

        //到此处账号存在，进行密码匹配
        boolean matches = passwordEncoder.matches(password, userByUserName.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("账号或密码错误");
        }
        //判断用户状态
        if (!Constants.User.DEFAULT_STATE.equals(userByUserName.getState())) {
            return ResponseResult.ACCOUNT_DENIED();
        }
        //密码正确，生成token
        createToken(response, userByUserName);
        return ResponseResult.LOGIN_SUCCESS();
    }

    /**
     * 创建新的token和refreshToken，分别存储到redis和数据库中
     * @param response
     * @param blogUser
     * @return
     */
    private String createToken(HttpServletResponse response, BlogUser blogUser) {
        //删除旧的refreshToken记录
        int deleteResult = refreshTokenMapper.deleteAllByUserId(blogUser.getId());
        log.info("createToken =====> 删除旧refreshToken的结果："+deleteResult);
        //token的默认有效时长为2小时
        Map<String, Object> claims = ClaimsUtil.blogUser2Claims(blogUser);
        String token = JwtUtil.createToken(claims);
        //生成token的MD5值
        String tokenMD5 = DigestUtils.md5DigestAsHex(token.getBytes());
        //将token存储到redis，MD5值作为key，token作为value，时长2小时
        redisUtil.set(Constants.User.KEY_TOKEN_MD5 + tokenMD5, token, 2 * Constants.TimeValue.HOUR);
        //将tokenMD5写到Cookie中
        CookieUtil.setUpCookie(response, Constants.User.KEY_COOKIE_TOKEN, tokenMD5);
        //生成refreshToken，写到数据库中
        String refreshTokenValue = JwtUtil.createRefreshToken(blogUser.getId(), 7 * Constants.TimeValue.DAY);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setTokenKey(tokenMD5);
        refreshToken.setUserId(blogUser.getId());
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenMapper.insertOneRefreshToken(refreshToken);
        return tokenMD5;
    }

    /**
     * 登录逻辑检查
     * 通过携带的token来进行登录检查
     *
     * @return
     */
    @Override
    public BlogUser checkBlogUser() {
        //从Cookie中拿到tokenMD5值
        String tokenMd5 = CookieUtil.getCookie(getRequest(), Constants.User.KEY_COOKIE_TOKEN);
        BlogUser blogUser = parseByTokenKey(tokenMd5);
        if (blogUser == null) {
            log.info("checkBlogUser    blogUser为空");
            //解析出错，token过期
            //去数据库中查找refreshToken
            RefreshToken refreshToken = refreshTokenMapper.findOneByTokenKey(tokenMd5);
            if (refreshToken == null) {
                //refreshToken不存在，说明未登录过，返回空
                return null;
            }
            try {
                //refreshToken存在，解析refreshToken
                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                String userId = refreshToken.getUserId();
                BlogUser blogUserFromDb = blogUserMapper.findOneById(userId);
                //创建新的token存储到redis中，新的refreshToken存储到数据库中
                String newTokenMD5 = createToken(getResponse(), blogUserFromDb);
                return parseByTokenKey(newTokenMD5);
            }catch (Exception e){
                //refreshToken过期
                log.info("checkBlogUser    refreshToken="+refreshToken+"  已过期");
                return null;
            }
        }
        return blogUser;
    }

    private HttpServletRequest getRequest(){
        //从容器中拿到request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    private HttpServletResponse getResponse(){
        //从容器中拿到response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    /**
     * 从redis中查询解析token
     * @param tokenMd5
     * @return
     */
    private BlogUser parseByTokenKey(String tokenMd5) {
        //使用tokenMD5到Redis中查询token
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN_MD5 + tokenMd5);
        if (token != null) {
            //解析token
            try {
                Claims claims = JwtUtil.parseJWT(token);
                BlogUser blogUser = ClaimsUtil.claims2BlogUser(claims);
                log.info("parseByTokenKey    blogUser="+blogUser.getUserName());
                return blogUser;
            } catch (Exception e) {
                //token过期
                log.info("parseByTokenKey    token="+token+"  已过期");
                return null;
            }
        }
        return null;
    }

    /**
     * 获取用户信息，使用Gson复制对象
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getUserInfo(String userId) {
        //1.先从数据库中查询
        BlogUser userById = blogUserMapper.findOneById(userId);
        //2.判空
        if (userById == null) {
            return ResponseResult.FAILED("用户不存在");
        }
        //用户存在，复制对象，然后将password、email、ip、time设置空
        String toJson = gson.toJson(userById);
        BlogUser newBlogUser = gson.fromJson(toJson, BlogUser.class);
        newBlogUser.setPassword("");
        newBlogUser.setEmail("");
        newBlogUser.setState("");
        newBlogUser.setLoginIp("");
        newBlogUser.setRegIp("");
        newBlogUser.setCreateTime(null);
        newBlogUser.setUpdateTime(null);
        return ResponseResult.SUCCESS("获取用户信息成功").setData(newBlogUser);
    }

    /**
     * 检查邮箱是否重复
     * @param email
     * @return
     */
    @Override
    public ResponseResult checkEmail(String email) {
        BlogUser userByEmail = blogUserMapper.findOneByEmail(email);
        return userByEmail==null?ResponseResult.SUCCESS("该邮箱未注册"):ResponseResult.FAILED("该邮箱已注册");
    }

    /**
     * 检查用户名是否重复
     * @param userName
     * @return
     */
    @Override
    public ResponseResult checkUserName(String userName) {
        BlogUser userByName = blogUserMapper.findOneByUserName(userName);
        return userByName==null?ResponseResult.SUCCESS("用户名可以使用"):ResponseResult.FAILED("用户名已重复");
    }

    /**
     * 更新用户信息
     * @param userId
     * @param blogUser
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(String userId, BlogUser blogUser) {
        //先进行账号登录检查,从token中取出数据进行校验
        BlogUser userFromToken = checkBlogUser();
        if (userFromToken == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //从数据库中查出用户
        BlogUser userById = blogUserMapper.findOneById(userFromToken.getId());
        if (!userById.getId().equals(userId)) {
            return ResponseResult.PERMISSION_DENIED();
        }
        //可以进行修改了
        //修改用户名
        String userName = blogUser.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            BlogUser oneByUserName = blogUserMapper.findOneByUserName(userName);
            if (oneByUserName != null) {
                return ResponseResult.FAILED("用户名重复");
            }
            userById.setUserName(userName);
        }
        //修改头像
        if(!TextUtils.isEmpty(blogUser.getAvatar())){
            userById.setAvatar(blogUser.getAvatar());
        }
        //修改签名
        userById.setSign(blogUser.getSign());
        userById.setUpdateTime(new Date());
        blogUserMapper.updateUserByCondition(userById);


        //删除Redis里保存的旧token信息，下次登录检查时会从数据库查询refreshToken然后把最新token存储到redis中
        String tokenMD5 = CookieUtil.getCookie(getRequest(), Constants.User.KEY_COOKIE_TOKEN);
        redisUtil.del(Constants.User.KEY_TOKEN_MD5 + tokenMD5);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }

    /**
     * 根据ID删除用户,要进行权限判断
     * @param userId
     * @return
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        int result = blogUserMapper.deleteUserByState(userId);
        return result>0?ResponseResult.SUCCESS("用户删除成功"):ResponseResult.FAILED("用户删除失败");
    }

    /**
     * 管理员权限获取用户列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listUsers(int page, int size) {
        //分页查询
        page = Math.max(page, Constants.Page.DEFAULT_PAGE);
        size = Math.max(size,Constants.Page.DEFAULT_SIZE);

        List<BlogUser> all = blogUserMapper.getUserByPage((page - 1) * size, size);
        return ResponseResult.SUCCESS("用户列表获取成功").setData(all);
    }

    /**
     * 更新用户密码
     * @param verifyCode
     * @param blogUser
     * @return
     */
    @Override
    public ResponseResult updateUserPassword(String verifyCode, BlogUser blogUser) {
        //检查邮箱
        String email = blogUser.getEmail();
        if (TextUtils.isEmpty(email) || !TextUtils.isEmailAddressOk(email)) {
            return ResponseResult.FAILED("邮箱格式不正确");
        }
        //从redis中拿去邮箱验证码
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if (emailVerifyCode == null) {
            return ResponseResult.FAILED("邮箱验证码已失效或邮箱地址不正确");
        }
        if(verifyCode == null || !verifyCode.equals(emailVerifyCode)){
            return ResponseResult.FAILED("邮箱验证码错误");
        }
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        int result = blogUserMapper.updatePasswordByEmail(passwordEncoder.encode(blogUser.getPassword()), email);
        return result>0?ResponseResult.SUCCESS("密码修改成功"):ResponseResult.FAILED("密码修改失败");
    }

    /**
     * 更新邮箱
     * @param email
     * @param verifyCode
     * @return
     */
    @Override
    public ResponseResult updateEmail(String email, String verifyCode) {
        //确保登录了
        BlogUser currentUser = checkBlogUser();
        if (currentUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //对比验证码，检查邮箱
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        if(TextUtils.isEmpty(emailVerifyCode) || !verifyCode.equals(emailVerifyCode)){
            return ResponseResult.FAILED("邮箱验证码过期或者错误");
        }
        //验证码正确，从Redis中删除验证码
        redisUtil.del(Constants.User.KEY_EMAIL_CODE_CONTENT + email);
        //更新邮箱
        int result = blogUserMapper.updateEmailById(email, currentUser.getId());
        return result>0?ResponseResult.SUCCESS("邮箱修改成功"):ResponseResult.FAILED("邮箱修改失败");
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public ResponseResult doLogout() {
        String tokenMD5 = CookieUtil.getCookie(getRequest(), Constants.User.KEY_COOKIE_TOKEN);
        if(TextUtils.isEmpty(tokenMD5)){
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //删除Cookie里的tokenMD5
        CookieUtil.deleteCookie(getResponse(),Constants.User.KEY_COOKIE_TOKEN);
        //删除redis里的token
        redisUtil.del(Constants.User.KEY_TOKEN_MD5 + tokenMD5);
        //删除mysql里的refreshToken
        refreshTokenMapper.deleteAllByTokenKey(tokenMD5);
        return ResponseResult.SUCCESS("账号退出成功");
    }
}
