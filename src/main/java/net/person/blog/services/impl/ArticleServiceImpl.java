package net.person.blog.services.impl;

import net.person.blog.dao.ArticleMapper;
import net.person.blog.pojo.Article;
import net.person.blog.pojo.BlogUser;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IArticleService;
import net.person.blog.services.IUserService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements IArticleService {

    @Autowired
    private IUserService userService;

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private ArticleMapper articleMapper;


    /**
     * 上传文章
     * 保存成草稿
     * 1.用户手动提交，不会发生页面跳转
     * 2.机器自动提交，每隔一段时间会自动提交一次
     *
     * 推荐方案：
     * 机器自动提交的草稿保存到本地，用户手动提交的草稿保存到后台
     *
     * 防止重复提交（网络卡顿，用户点了好几次）
     * 1.通过ID的方式来判断
     * 2.通过token的请求频率来判断，如果30s内多次重复提交只算最前的那一次提交
     *
     * 前端处理：点击了提交禁止按钮，等有响应结果再改变按钮状态
     * @param article
     * @return
     */
    @Override
    public ResponseResult postArticle(Article article) {
        //获取用户对象
        BlogUser blogUser = userService.checkBlogUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //获取文章状态
        String articleState = article.getState();
        if (TextUtils.isEmpty(articleState)) {
            return ResponseResult.FAILED("文章状态错误");
        }
        //检查文章状态，是草稿还是发布
        if (!Constants.Article.STATE_PUBLISH.equals(articleState) &&
                !Constants.Article.STATE_DRAFT.equals(articleState)) {
            //不支持此操作
            return ResponseResult.FAILED("不支持此操作");
        }
        //每次都要判断文章类型，不管是草稿还是发布
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("文章类型不能为空");
        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("文章类型不正确");
        }
        //以下检查是发布检查，草稿不需要进行检查
        if (Constants.Article.STATE_PUBLISH.equals(articleState)) {
            //检查数据
            //title、分类ID、内容、类型、摘要、标签
            String title = article.getTitle();
            if (TextUtils.isEmpty(title)) {
                return ResponseResult.FAILED("文章标题不能为空");
            }
            //判断标题长度
            if (title.length()> Constants.Article.TITLE_MAX_LENGTH) {
                return ResponseResult.FAILED("文章标题不能超过"+Constants.Article.TITLE_MAX_LENGTH+"个字符");
            }
            String categoryId = article.getCategoryId();
            if (TextUtils.isEmpty(categoryId)) {
                return ResponseResult.FAILED("文章分类不能为空");
            }
            String content = article.getContent();
            if (TextUtils.isEmpty(content)) {
                return ResponseResult.FAILED("文章内容不能为空");
            }
            String summary = article.getSummary();
            if (TextUtils.isEmpty(summary)) {
                return ResponseResult.FAILED("文章摘要不能为空");
            }
            //判断摘要长度
            if (summary.length()> Constants.Article.SUMMARY_MAX_LENGTH) {
                return ResponseResult.FAILED("文章摘要不能超过"+Constants.Article.SUMMARY_MAX_LENGTH+"个字符");
            }
            String labels = article.getLabel();
            if (TextUtils.isEmpty(labels)) {
                return ResponseResult.FAILED("文章标签不能为空");
            }
        }
        //补全数据，文章ID，用户ID，创建时间，更新时间
        String id = article.getId();
        //id为空说明是新文章而不是草稿
        if(TextUtils.isEmpty(id)){
            article.setId(idWorker.nextId()+"");
            article.setCreateTime(new Date());
            article.setUserId(blogUser.getId());
            article.setUpdateTime(new Date());
            articleMapper.insertArticle(article);
        }else {
            //更新内容：对状态进行处理，如何已经发布了就不能在保存为草稿
            Article articleFromDb = articleMapper.findOneById(id);
            if (Constants.Article.STATE_PUBLISH.equals(articleFromDb.getState()) &&
                    Constants.Article.STATE_DRAFT.equals(articleState)) {
                //已经发布过了，只能更新，不能保存为草稿
                return ResponseResult.FAILED("该文章已发布，不能保存为草稿");
            }
            article.setUpdateTime(new Date());
            articleMapper.updateArticleByConditions(article);
        }
        //做成系统自动保存就需要返回ID，否则每次都会创建新的ID
        return ResponseResult.SUCCESS(Constants.Article.STATE_DRAFT.equals(articleState)?"草稿保存成功":"文章发表成功").setData(article.getId());
    }

    /**
     * 获取文章列表
     * @param page 页码
     * @param size 每一页数量
     * @param state 文章状态
     * @param keyword  文章标题关键字
     * @param categoryId 分类ID
     * @return
     */
    @Override
    public ResponseResult listArticles(int page, int size, String state, String keyword, String categoryId) {
        page = Math.max(Constants.Page.DEFAULT_PAGE,page);
        size = Math.max(Constants.Page.DEFAULT_SIZE,size);
        List<Article> all = articleMapper.getArticlesNoContentByConditions((page - 1) * size, size, state, categoryId, "%" + keyword + "%");
        return ResponseResult.SUCCESS("获取文章列表成功").setData(all);
    }

    /**
     * 获取单篇文章
     * 有草稿、有删除的、有置顶的、有已发布的
     * 删除不能获取，其他都可以
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult getArticle(String articleId) {
        Article article = articleMapper.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        //判断文章状态
        String state = article.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            return ResponseResult.SUCCESS("获取文章信息成功").setData(article);
        }
        BlogUser blogUser = userService.checkBlogUser();
        if (blogUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //草稿状态，需要属于本人角色
        if(Constants.Article.STATE_DRAFT.equals(state)){
            if (article.getUserId().equals(blogUser.getId())) {
                return ResponseResult.SUCCESS("获取文章成功").setData(article);
            }
        }
        //删除状态，管理员可以获取
        String roles = blogUser.getRoles();
        if(!Constants.User.ROLE_ADMIN.equals(roles)){
            return ResponseResult.PERMISSION_DENIED();
        }
        return ResponseResult.SUCCESS("获取文章成功").setData(article);
    }

    /**
     * 更新文章内容
     * 该接口支持修改的内容：标题、内容、标签、分类、摘要
     * @param articleId
     * @param article
     * @return
     */
    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        //查询文章存不存在
        Article articleFromDb = articleMapper.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        article.setId(articleId);
        article.setUpdateTime(new Date());
        articleMapper.updateArticleByConditions(article);
        return ResponseResult.SUCCESS("更新文章成功");
    }

    /**
     * 从数据库删除文章
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteArticle(String articleId) {
        //管理员可以直接删除文章
        int result = articleMapper.deleteOneById(articleId);
        return result > 0 ? ResponseResult.SUCCESS("文章删除成功"):ResponseResult.FAILED("文章不存在");
    }

    /**
     * 通过状态删除文章
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteArticleByUpdateState(String articleId) {
        int result = articleMapper.deleteArticleByUpdateState(articleId);
        return result > 0 ? ResponseResult.SUCCESS("文章删除成功"):ResponseResult.FAILED("文章不存在");
    }

    /**
     * 修改文章状态为置顶
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult topArticle(String articleId,String top) {
        Article articleFromDb = articleMapper.findOneById(articleId);
        if(articleFromDb == null){
            return ResponseResult.FAILED("文章不存在");
        }
        //只有发布的文章才能改变置顶状态
        String state = articleFromDb.getState();
        if(Constants.Article.STATE_PUBLISH.equals(state)){
            if(Constants.Article.TOP_TRUE.equals(top) || Constants.Article.TOP_FALSE.equals(top)) {
                articleMapper.updateArticleTop(articleId, top);
                return ResponseResult.SUCCESS("文章置顶成功");
            }
        }
        //其他情况无权操作
        return  ResponseResult.FAILED("不支持该操作");
    }


    /**
     * 获取置顶文章
     * @return
     */
    @Override
    public ResponseResult listTopArticles() {
        List<Article> all = articleMapper.findArticlesByTop(Constants.Article.TOP_TRUE);
        return ResponseResult.SUCCESS("获取置顶文章成功").setData(all);
    }

    /**
     * 获取推荐文章
     * @param articleId
     * @param size
     * @return
     */
    @Override
    public ResponseResult listRecommendArticles(String articleId, String size) {
        //不需要文章内容，只需要标签
        String labelById = articleMapper.findLabelById(articleId);
        return null;
    }
}
