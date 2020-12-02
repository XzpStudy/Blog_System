package net.person.blog.controller.admin;


import net.person.blog.pojo.Article;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
public class ArticleAdminApi {


    @Autowired
    private IArticleService articleService;

    /**
     * 上传文章
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article){
        return articleService.postArticle(article);
    }

    /**
     * 如果是多用户，用户的删除只是修改状态，不是真的删除
     * 管理员是真的可以删除
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId")String articleId){
        return articleService.deleteArticle(articleId);
    }

    /**
     * 更新文章内容
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId")String articleId,@RequestBody Article article){
        return articleService.updateArticle(articleId,article);
    }

    /**
     * 获取单篇文章
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId")String articleId){
        return articleService.getArticle(articleId);
    }

    /**
     * 获取文章列表
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticles(@PathVariable("page")int page,
                                       @PathVariable("size")int size,
                                       @RequestParam(value = "state",required = false)String state,
                                       @RequestParam(value = "keyword",required = false)String keyword,
                                       @RequestParam(value = "categoryId",required = false)String categoryId){
        return articleService.listArticles(page,size,state,keyword,categoryId);
    }


    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/state/{articleId}")
    public ResponseResult deleteArticleByUpdateState(@PathVariable("articleId")String articleId){
        return articleService.deleteArticleByUpdateState(articleId);
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/top/{articleId}/{top}")
    public ResponseResult topArticle(@PathVariable("articleId")String articleId,@PathVariable("top")String top){
        return articleService.topArticle(articleId,top);
    }
}
