package net.person.blog.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 * @author 16272
 */
public class CookieUtil {
    //1年
    public static final int DEFAULT_AGE = 60 * 60 * 24 * 365;

    public static final String DOMAIN = "localhost";

    /**
     * 设置cookie值
     *
     * @param response
     * @param key
     * @param value
     */
    public static void setUpCookie(HttpServletResponse response, String key, String value) {
        setUpCookie(response, key, value, DEFAULT_AGE);
    }

    public static void setUpCookie(HttpServletResponse response, String key, String value, int age) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(DOMAIN);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    /**
     * 删除cookie
     *
     * @param response
     * @param key
     */
    public static void deleteCookie(HttpServletResponse response, String key) {
        setUpCookie(response, key, null, 0);
    }

    /**
     * 获取cookie
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
