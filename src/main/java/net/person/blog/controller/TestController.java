package net.person.blog.controller;


import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.BlogUserMapper;
import net.person.blog.pojo.BlogUser;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IUserService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    private BlogUserMapper blogUserMapper;

//    http://localhost:88/test/captcha
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        // specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        specCaptcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        //specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        // 验证码存入session
        //request.getSession().setAttribute("captcha", content);
        //验证码存入Redis
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT,content,60*10);

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

    @GetMapping("/getTokenKey")
    public ResponseResult getTokenKey(@RequestParam("key")String key){
        //将传过来的key加密后作为token的key
        String tokenKey = DigestUtils.md5DigestAsHex(key.getBytes());
        log.info("jwt ====>   tokenKey ====>"+tokenKey);
        return ResponseResult.SUCCESS();
    }

    @PostMapping("/check")
    public ResponseResult checkLogin(){
        BlogUser blogUser = userService.checkBlogUser();
        log.info("check   blogUser=====>  "+ blogUser);
        return blogUser == null?ResponseResult.ACCOUNT_NOT_LOGIN():ResponseResult.SUCCESS("登录成功");
    }

    @GetMapping("/getUserNoPassword")
    public ResponseResult getUserNoPassword(@RequestParam("id")String id){
        BlogUser oneById = blogUserMapper.findOneById(id);
        System.out.println("test ====>  oneById ====>  "+oneById);
        return ResponseResult.SUCCESS().setData(oneById);
    }
}
