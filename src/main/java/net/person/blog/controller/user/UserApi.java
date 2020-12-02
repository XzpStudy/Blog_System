package net.person.blog.controller.user;

import lombok.extern.slf4j.Slf4j;
import net.person.blog.pojo.BlogUser;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

/**
 * @author 16272
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号
     *
     * @param blogUser
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody BlogUser blogUser, HttpServletRequest request){
        log.info("喔啦啦乌拉拉");
        return userService.initManagerAccount(blogUser,request);
    }

    /**
     * 注册用户
     * 发送邮箱验证码时指定type=register
     * @param blogUser
     * @return
     */
    @PostMapping("/join_in")
    public ResponseResult register(@RequestBody BlogUser blogUser,
                                   @RequestParam("email_code")String emailCode,
                                   @RequestParam("captcha_code")String captchaCode,
                                   @RequestParam("captcha_key")String captchaKey,
                                   HttpServletRequest request){
        return userService.register(blogUser,emailCode,captchaCode,captchaKey,request);
    }

    /**
     * 登录
     * 需要提交的数据
     * 1.用户账号：昵称或邮箱，都做了唯一处理
     * 2.密码
     * 3.图灵验证码
     * 4.图灵验证码的key
     *
     * @param captcha
     * @param blogUser
     * @param captchaKey
     * @return
     */
    @PostMapping("login/{captcha}/{captcha_key}")
    public ResponseResult login(@PathVariable("captcha_key")String captchaKey,
                                @PathVariable("captcha") String captcha,
                                @RequestBody BlogUser blogUser,
                                HttpServletRequest request,
                                HttpServletResponse response){
        return userService.doLogin(captcha,captchaKey,blogUser,request,response);
    }

    /**
     * 获取图灵验证码
     * @return
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response,@RequestParam("captcha_key")String captchaKey){
        try {
            userService.createCaptcha(response,captchaKey);
        } catch (IOException | FontFormatException e) {
            log.error(e.toString());
        }
    }

    /**
     * 发送邮箱验证码
     * 需要指定type属性
     * 注册(register)
     * 修改密码(forget)
     * 更新邮箱(update)
     * @param emailAddress
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("type")String type,HttpServletRequest request,@RequestParam("email") String emailAddress){
        log.info("邮箱为：=====》"+emailAddress);
        return  userService.sendEmail(type,request,emailAddress);
    }

    /**
     * 修改密码：有俩种情况，一种是忘记密码，一种是登录后修改
     * 需要邮箱验证码和邮箱
     * 发送邮箱验证码时指定type=forget
     * @param blogUser
     * @return
     */
    @PutMapping("/password/{verifyCode}")
    public ResponseResult updatePassword(@PathVariable("verifyCode") String verifyCode,@RequestBody BlogUser blogUser){
        return userService.updateUserPassword(verifyCode,blogUser);
    }

    /**
     * 获取作者信息
     * @param userId
     * @return
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId){
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     * 允许修改的内容：
     * 1.头像
     * 2.用户名
     * 4.签名
     * 密码和邮箱单独修改
     *
     * @param blogUser
     * @return
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId")String userId, @RequestBody BlogUser blogUser){
        return userService.updateUserInfo(userId,blogUser);
    }


    /**
     * 获取用户列表
     * 有管理员权限才可以获取
     * PreAuthorize为权限控制注解
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page")int page,
                                    @RequestParam("size")int size){
        return userService.listUsers(page,size);
    }

    /**
     * 删除用户
     * 需要权限才可以删除
     * PreAuthorize为权限控制注解
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/userInfo/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId")String userId){
        return userService.deleteUserById(userId);
    }

    /**
     * 用于实时检查邮箱是否重复
     * @param email
     * @return
     */
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email")String email){
        return userService.checkEmail(email);
    }

    /**
     * 用于实时检查用户名是否重复
     * @param userName
     * @return
     */
    @GetMapping("/user_name")
    public ResponseResult checkUserName(@RequestParam("userName")String userName){
        return userService.checkUserName(userName);
    }

    /**
     * 修改邮箱地址
     * 1.必须登录
     * 2.新邮箱没被注册过
     * 3.获取验证码
     * 发送邮箱验证码时指定type=update
     * @return
     */
    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("email")String email,@RequestParam("verifyCode")String verifyCode){
        return userService.updateEmail(email,verifyCode);
    }

    /**
     * 退出登录
     * 删除redis里的token
     * 删除mysql里的refreshToken
     * 删除cookie里的tokenMD5
     */
    @GetMapping("/logout")
    public ResponseResult logout(){
        return userService.doLogout();
    }
}
