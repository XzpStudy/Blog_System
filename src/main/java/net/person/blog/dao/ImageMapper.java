package net.person.blog.dao;

import net.person.blog.pojo.Image;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImageMapper {

    Image findOneById(String imageId);

    int deleteImageByUpdateState(String imageId);

    int insertImageMessage(Image image);

    List<Image> getImagesByConditions(@Param("startIndex") int startIndex, @Param("size")int size,
                                      @Param("userId")String userId, @Param("state")String state);

}
