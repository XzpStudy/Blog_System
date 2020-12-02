package net.person.blog.controller.portal;


import net.person.blog.pojo.Comment;
import net.person.blog.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalApi {
    /**
     *
     */
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return null;
    }

    /**
     *
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId")String commentId){
        return null;
    }

//    @PutMapping("/{commentId}")
//    public ResponseResult updateComment(@PathVariable("commentId")String commentId){
//        return null;
//    }

    @GetMapping("/list/{articleId}")
    public ResponseResult listComments(@PathVariable("articleId")String articleId){
        return null;
    }

}
