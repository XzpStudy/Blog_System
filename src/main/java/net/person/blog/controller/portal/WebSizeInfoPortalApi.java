package net.person.blog.controller.portal;

import net.person.blog.response.ResponseResult;
import net.person.blog.services.ICategoryService;
import net.person.blog.services.IFriendLinkService;
import net.person.blog.services.ILoopService;
import net.person.blog.services.IWebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalApi {


    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IFriendLinkService friendLinkService;

    @Autowired
    private ILoopService loopService;

    @Autowired
    private IWebSizeInfoService webSizeInfoService;

    /**
     * 普通用户或者未登录用户获取分类标签
     * @return
     */
    @GetMapping("/categories")
    public ResponseResult getCategories(){
        return categoryService.getCommonCategory();
    }


    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return webSizeInfoService.getWebSizeTitle();
    }

    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return webSizeInfoService.getSizeViewCount();
    }

    @GetMapping("/seo")
    public ResponseResult getWebSizeSeoInfo(){
        return webSizeInfoService.getSeoInfo();
    }

    /**
     * 获取轮播图
     * @return
     */
    @GetMapping("/loop")
    public ResponseResult getLoops(){
        return loopService.getCommonLoops();
    }

    /**
     * 获取友情链接
     * @return
     */
    @GetMapping("/friend_link")
    public ResponseResult getLinks(){
        return friendLinkService.getCommonLinks();
    }
}
