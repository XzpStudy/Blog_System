package net.person.blog.services;

import net.person.blog.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IImageService {
    ResponseResult uploadImage(MultipartFile file);

    void viewImage(HttpServletResponse response, String imageId) throws IOException;

    ResponseResult listImages(int page, int size);

    ResponseResult deleteImage(String imageId);
}
