package net.person.blog.controller.admin;


import net.person.blog.pojo.FriendLink;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IFriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkAdminApi {

    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 添加友情链接
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendLink){
        return friendLinkService.addFriendLink(friendLink);
    }

    /**
     * 删除友情链接
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId")String friendLinkId){
        return friendLinkService.deleteFriendLink(friendLinkId);
    }

    /**
     * 修改友情链接
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId")String friendLinkId,@RequestBody FriendLink friendLink){
        return friendLinkService.updateFriendLink(friendLinkId,friendLink);
    }

    /**
     * 获取友情链接
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkId")String friendLinkId){
        return friendLinkService.getFriendLink(friendLinkId);
    }

    /**
     * 获取友情链接集合
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listFriendLinks(){
        return friendLinkService.listFriendLinks();
    }
}
