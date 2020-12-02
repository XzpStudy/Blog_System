package net.person.blog.services.impl;

import net.person.blog.dao.CommentMapper;
import net.person.blog.pojo.BlogUser;
import net.person.blog.pojo.Comment;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.ICommentService;
import net.person.blog.services.IUserService;
import net.person.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements ICommentService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private IUserService userService;

    /**
     * 评论置顶
     * @param commentId
     * @return
     */
    @Override
    public ResponseResult topComment(String commentId,String top) {
        Comment comment = commentMapper.findOneById(commentId);
        if (comment == null) {
            return ResponseResult.FAILED("评论不存在.");
        }
        if(Constants.Comment.STATE_PUBLISH.equals(comment.getState())) {
            if(Constants.Comment.TOP_FALSE.equals(top) || Constants.Comment.TOP_TRUE.equals(top)){
                commentMapper.updateCommentTop(commentId, top);
                return Constants.Comment.TOP_FALSE.equals(top)?ResponseResult.SUCCESS("取消评论置顶成功"):ResponseResult.SUCCESS("评论置顶成功");
            }
        }
        return ResponseResult.FAILED("评论状态非法.");
    }

    /**
     * 获取评论
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listComments(int page, int size) {
        page = Math.max(Constants.Page.DEFAULT_PAGE,page);
        size = Math.max(Constants.Page.DEFAULT_SIZE,size);
        List<Comment> all = commentMapper.getCommentsByPage((page - 1) * size, size);
        return ResponseResult.SUCCESS("获取评论列表成功.").setData(all);
    }

    @Override
    public ResponseResult deleteCommentById(String commentId) {
        //检查用户角色
        BlogUser blogUser = userService.checkBlogUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //把评论找出来，比对用户权限
        Comment comment = commentMapper.findOneById(commentId);
        if (comment == null) {
            return ResponseResult.FAILED("评论不存在.");
        }
        if (blogUser.getId().equals(comment.getUserId()) ||
                //登录要判断角色
                Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            commentMapper.deleteCommentByUpdateState(commentId);
            return ResponseResult.SUCCESS("评论删除成功.");
        } else {
            return ResponseResult.PERMISSION_DENIED();
        }
    }
}
