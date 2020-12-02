package net.person.blog.controller.admin;


import net.person.blog.pojo.Category;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 文章分类接口
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     * 需要管理员权限
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return categoryService.add(category);
    }

    /**
     * 删除分类
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 更新分类
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId,@RequestBody Category category){
        return categoryService.updateCategory(categoryId,category);
    }

    /**
     * 获取分类
     *
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取分类列表
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listCategories(@PathVariable("page")int page,@PathVariable("size")int size){
        return categoryService.listCategories(page,size);
    }
}
