package net.person.blog.controller.admin;


import net.person.blog.response.ResponseResult;
import net.person.blog.services.IWebSizeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/web_size_info")
public class WebSizeInfoAdminApi {

    @Autowired
    private IWebSizeInfoService webSizeInfoService;

    @PreAuthorize("@permission.admin()")
    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return webSizeInfoService.getWebSizeTitle();
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/title")
    public ResponseResult upWebSizeTitle(@RequestParam("title")String title){
        return webSizeInfoService.putWebSizeTitle(title);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/seo")
    public ResponseResult getSeoInfo(){
        return webSizeInfoService.getSeoInfo();
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords")String keywords,@RequestParam("description")String description){
        return webSizeInfoService.putSeoInfo(keywords,description);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return webSizeInfoService.getSizeViewCount();
    }
}
