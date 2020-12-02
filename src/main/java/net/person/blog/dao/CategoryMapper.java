package net.person.blog.dao;

import net.person.blog.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryMapper {

    Category findOneById(String categoryId);

    int deleteCategoryByUpdateState(String categoryId);

    List<Category> listCommonCategoryByState();

    int insertCategory(Category category);

    List<Category> getCategoriesByPage(@Param("startIndex") int startIndex,@Param("size") int size);

    int updateCategoryByCondition(Category category);
}
