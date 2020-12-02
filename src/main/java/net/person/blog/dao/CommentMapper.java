package net.person.blog.dao;

import net.person.blog.pojo.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentMapper {


    Comment findOneById(String commentId);

    int updateCommentTop(@Param("id") String commentId, @Param("top")String top);

    List<Comment> getCommentsByPage(@Param("startIndex")int startIndex,@Param("size")int size);

    int deleteCommentByUpdateState(String commentId);
}
