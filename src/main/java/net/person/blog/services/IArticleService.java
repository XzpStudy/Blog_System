package net.person.blog.services;

import net.person.blog.pojo.Article;
import net.person.blog.response.ResponseResult;

public interface IArticleService {
    ResponseResult postArticle(Article article);

    ResponseResult listArticles(int page, int size, String state, String keyword, String categoryId);

    ResponseResult getArticle(String articleId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticle(String articleId);

    ResponseResult topArticle(String articleId,String top);

    ResponseResult deleteArticleByUpdateState(String articleId);

    ResponseResult listTopArticles();

    ResponseResult listRecommendArticles(String articleId, String size);
}
