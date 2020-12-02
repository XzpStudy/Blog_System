package net.person.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.CategoryMapper;
import net.person.blog.pojo.Category;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.ICategoryService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加文章分类
     * @param category
     * @return
     */
    @Override
    public ResponseResult add(Category category) {
        //检查数据
        if (TextUtils.isEmpty(category.getName())) {
            return ResponseResult.FAILED("分类名称不可以为空");
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类拼音不可以为空");
        }
        if (TextUtils.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED("分类描述不可以为空");
        }
        //不全数据
        category.setId(idWorker.nextId()+"");
        category.setStatus("1");
        category.setOrders(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        //保存数据
        categoryMapper.insertCategory(category);
        return ResponseResult.SUCCESS("添加分类成功");
    }

    /**
     * 获取分类
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult getCategory(String categoryId) {
        Category category = categoryMapper.findOneById(categoryId);
        if (category == null) {
            return ResponseResult.FAILED("获取分类失败，分类不存在");
        }
        return ResponseResult.FAILED("获取分类成功").setData(category);
    }

    /**
     * 获取分类列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listCategories(int page, int size) {
        //参数检查
        page = Math.max(page, Constants.Page.DEFAULT_PAGE);
        size = Math.max(size, Constants.Page.DEFAULT_SIZE);
        List<Category> all = categoryMapper.getCategoriesByPage((page - 1) * size, size);
        return ResponseResult.SUCCESS("获取分页列表成功").setData(all);
    }

    /**
     * 更新分类
     * @param categoryId
     * @param category
     * @return
     */
    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        Category categoryFromDb = categoryMapper.findOneById(categoryId);
        if (categoryFromDb == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        category.setId(categoryId);
        category.setUpdateTime(new Date());
        //保存
        categoryMapper.updateCategoryByCondition(category);
        return ResponseResult.SUCCESS("分类更新成功");
    }

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryMapper.deleteCategoryByUpdateState(categoryId);
        return result>0?ResponseResult.SUCCESS("分类删除成功"):ResponseResult.FAILED("该分类不存在");
    }

    /**
     * 普通用户和未登录用户获取所有状态为"1"的分类标签
     * @return
     */
    @Override
    public ResponseResult getCommonCategory() {
        List<Category> categories = categoryMapper.listCommonCategoryByState();
        return ResponseResult.SUCCESS("获取分类成功").setData(categories);
    }
}
