package net.person.blog.services;

import net.person.blog.pojo.BlogUser;
import net.person.blog.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

public interface IUserService {

    ResponseResult initManagerAccount(BlogUser blogUser, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response,String captchaKey) throws IOException, FontFormatException;

    ResponseResult sendEmail(String type,HttpServletRequest request,String emailAddress);

    ResponseResult register(BlogUser blogUser,String emailCode,String captchaCode,String captchaKey,HttpServletRequest request);

    ResponseResult doLogin(String captcha,String captchaKey,BlogUser blogUser,HttpServletRequest request,HttpServletResponse response);

    BlogUser checkBlogUser();

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUserName(String userName);

    ResponseResult updateUserInfo(String userId, BlogUser blogUser);

    ResponseResult deleteUserById(String userId);

    ResponseResult listUsers(int page, int size);

    ResponseResult updateUserPassword(String verifyCode, BlogUser blogUser);

    ResponseResult updateEmail(String email, String verifyCode);

    ResponseResult doLogout();

}
