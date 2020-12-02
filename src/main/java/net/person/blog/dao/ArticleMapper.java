package net.person.blog.dao;

import net.person.blog.pojo.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ArticleMapper {

    Article findOneById(String id);

    Article findOneNoContent(String id);

    int deleteOneById(String articleId);

    int deleteArticleByUpdateState(String articleId);

    String findLabelById(String articleId);

    List<Article> getArticlesNoContentByConditions(@Param("startIndex") int startIndex, @Param("size") int size,
                                                   @Param("state") String state, @Param("categoryId") String categoryId,
                                                   @Param("title")String title);

    int updateArticleTop(@Param("id") String id,@Param("top")String top);

    List<Article> findArticlesByTop(String top);


    int insertArticle(Article article);

    int updateArticleByConditions(Article article);
}
