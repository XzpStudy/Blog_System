package net.person.blog.dao;

import net.person.blog.pojo.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public interface RefreshTokenMapper {

    /**
     * 通过tokenKey（token的MD5值）来查找refreshToken
     * @param tokenKey
     * @return
     */
    RefreshToken findOneByTokenKey(String tokenKey);

    int deleteAllByUserId(String userId);

    int deleteAllByTokenKey(String tokenKey);

    int insertOneRefreshToken(RefreshToken refreshToken);
}
