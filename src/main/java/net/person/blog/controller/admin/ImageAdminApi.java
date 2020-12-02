package net.person.blog.controller.admin;


import net.person.blog.response.ResponseResult;
import net.person.blog.services.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图片的相关接口
 */
@RestController
@RequestMapping("/admin/image")
public class ImageAdminApi {

    @Autowired
    private IImageService imageService;

    /**
     * 上传图片
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult uploadImage(@RequestParam("file")MultipartFile file){
        return imageService.uploadImage(file);
    }

    /**
     * 删除图片
     * @param imageId 图片id
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId")String imageId){
        return imageService.deleteImage(imageId);
    }


    /**
     * 获取图片
     * @param imageId 格式：时间戳_图片id.图片类型
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{imageId}")
    public void getImage(HttpServletResponse response, @PathVariable("imageId")String imageId){
        try {
            imageService.viewImage(response,imageId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片集合
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listImages(@PathVariable("page")int page,@PathVariable("size")int size){
        return imageService.listImages(page,size);
    }
}
