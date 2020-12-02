package net.person.blog.services;

import net.person.blog.pojo.FriendLink;
import net.person.blog.response.ResponseResult;

public interface IFriendLinkService {
    ResponseResult addFriendLink(FriendLink friendLink);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult listFriendLinks();

    ResponseResult deleteFriendLink(String friendLinkId);

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink);

    /**
     * 普通用户和未登录用户获取的友情链接
     * @return
     */
    ResponseResult getCommonLinks();
}
