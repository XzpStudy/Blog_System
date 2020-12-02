package net.person.blog.services;

import net.person.blog.response.ResponseResult;

public interface ICommentService {
    ResponseResult topComment(String commentId,String top);

    ResponseResult listComments(int page, int size);

    ResponseResult deleteCommentById(String commentId);
}
