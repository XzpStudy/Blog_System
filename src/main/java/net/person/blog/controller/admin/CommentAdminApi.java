package net.person.blog.controller.admin;


import net.person.blog.response.ResponseResult;
import net.person.blog.services.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

    @Autowired
    private ICommentService commentService;

    /**
     * 删除评论
     */
    @PreAuthorize(("@permission.admin()"))
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId")String commentId){
        return commentService.deleteCommentById(commentId);
    }


    /**
     * 获取评论合集
     */
    @PreAuthorize(("@permission.admin()"))
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listComments(@PathVariable("page")int page,@PathVariable("size")int size){
        return commentService.listComments(page, size);
    }


    /**
     * 评论置顶
     * @param commentId
     * @return
     */
    @PreAuthorize(("@permission.admin()"))
    @PutMapping("/top/{commentId}/{top}")
    public ResponseResult topComment(@PathVariable("commentId")String commentId,@PathVariable("top")String top){
        return commentService.topComment(commentId,top);
    }
}
