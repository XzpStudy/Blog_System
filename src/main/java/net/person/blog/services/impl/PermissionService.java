package net.person.blog.services.impl;

import net.person.blog.pojo.BlogUser;
import net.person.blog.services.IUserService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.CookieUtil;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限管理
 */
@Service("permission")
public class PermissionService {

    @Autowired
    private IUserService userService;

    /**
     * 判断是不是管理员
     * @return
     */
    public boolean admin(){
        //从容器中拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        //从Cookie中判断tokenMD5是否为空,为空则用户未登录或无令牌的cookie
        String tokenMD5 = CookieUtil.getCookie(request, Constants.User.KEY_COOKIE_TOKEN);
        if (TextUtils.isEmpty(tokenMD5)) {
            return false;
        }

        //进行用户权限检查
        BlogUser currentUser = userService.checkBlogUser();
        if (currentUser == null) {
            return  false;
        }
        if(!Constants.User.ROLE_ADMIN.equals(currentUser.getRoles())){
            return false;
        }
        return true;
    }
}
