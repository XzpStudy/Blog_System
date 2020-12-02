package net.person.blog.services;

import net.person.blog.pojo.Category;
import net.person.blog.response.ResponseResult;

public interface ICategoryService {

    ResponseResult add(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult listCategories(int page, int size);

    ResponseResult updateCategory(String categoryId, Category category);

    ResponseResult deleteCategory(String categoryId);

    /**
     * 普通用户和未登录用户获取所有分类标签
     * @return
     */
    ResponseResult getCommonCategory();
}
