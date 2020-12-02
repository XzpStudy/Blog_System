package net.person.blog.services;

import net.person.blog.response.ResponseResult;

public interface IWebSizeInfoService {
    ResponseResult getWebSizeTitle();

    ResponseResult putWebSizeTitle(String title);

    ResponseResult getSeoInfo();

    ResponseResult putSeoInfo(String keywords, String description);

    ResponseResult getSizeViewCount();

}
