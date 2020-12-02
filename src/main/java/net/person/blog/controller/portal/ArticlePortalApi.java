package net.person.blog.controller.portal;

import net.person.blog.response.ResponseResult;
import net.person.blog.services.IArticleService;
import net.person.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    @Autowired
    private IArticleService articleService;

    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page")int page,@PathVariable("size")int size){
        return articleService.listArticles(page,size, Constants.Article.STATE_PUBLISH,null,null);
    }

    @GetMapping("/list/{categoryId}/{page}/{size}")
    public ResponseResult listArticleByCategoryId(@PathVariable("categoryId")String categoryId,@PathVariable("page")int page,@PathVariable("size")int size){
        return articleService.listArticles(page,size, Constants.Article.STATE_PUBLISH,null,categoryId);
    }

    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId")String articleId){
        return articleService.getArticle(articleId);
    }

    /**
     * 通过标签来进行推荐
     * @param articleId
     * @return
     */
    @GetMapping("/recommend/{articleId}/{size}")
    public ResponseResult getRecommendArticles(@PathVariable("articleId")String articleId,@PathVariable("size")String size){
        return articleService.listRecommendArticles(articleId,size);
    }

    @GetMapping("/top")
    public ResponseResult getTopArticles(){
        return articleService.listTopArticles();
    }

    @GetMapping("/list/label/{label}/{page}/{size}")
    public ResponseResult listArticleByLabel(@PathVariable("label")String label,
                                             @PathVariable("page")int page,
                                             @PathVariable("size")int size){
        return null;
    }
}
