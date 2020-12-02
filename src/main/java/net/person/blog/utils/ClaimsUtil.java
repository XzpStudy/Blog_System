package net.person.blog.utils;

import net.person.blog.pojo.BlogUser;

import java.util.HashMap;
import java.util.Map;

/**
 * token工具类，便于设置和获取token中的属性
 */
public class ClaimsUtil {

    private static final String ID = "id";
    private static final String USER_NAME = "userName";
    private static final String ROLES = "roles";
    private static final String AVATAR = "avatar";
    private static final String EMAIL = "email";
    private static final String SIGN = "sign";


    public static Map<String,Object> blogUser2Claims(BlogUser blogUser){
        Map<String,Object> claims = new HashMap<>();
        claims.put(ID,blogUser.getId());
        claims.put(USER_NAME,blogUser.getUserName());
        claims.put(ROLES,blogUser.getRoles());
        claims.put(AVATAR,blogUser.getAvatar());
        claims.put(EMAIL,blogUser.getEmail());
        claims.put(SIGN,blogUser.getSign());
        return claims;
    }

    public static BlogUser claims2BlogUser(Map<String,Object> claims){
        BlogUser blogUser = new BlogUser();
        blogUser.setId((String) claims.get(ID));
        blogUser.setUserName((String) claims.get(USER_NAME));
        blogUser.setRoles((String) claims.get(ROLES));
        blogUser.setAvatar((String) claims.get(AVATAR));
        blogUser.setEmail((String) claims.get(EMAIL));
        blogUser.setSign((String) claims.get(SIGN));
        return blogUser;
    }

}
