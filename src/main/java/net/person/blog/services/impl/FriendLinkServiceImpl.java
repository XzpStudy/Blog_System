package net.person.blog.services.impl;

import net.person.blog.dao.FriendLinkMapper;
import net.person.blog.pojo.FriendLink;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IFriendLinkService;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendLinkServiceImpl implements IFriendLinkService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    /**
     * 添加友情链接
     * @param friendLink
     * @return
     */
    @Override
    public ResponseResult addFriendLink(FriendLink friendLink) {
        //判断数据
        String url = friendLink.getUrl();
        if (TextUtils.isEmpty(url)) {
            return ResponseResult.FAILED("链接不可以为空");
        }
        String logo = friendLink.getLogo();
        if (TextUtils.isEmpty(logo)) {
            return ResponseResult.FAILED("Logo不可以为空");
        }
        String name = friendLink.getName();
        if (TextUtils.isEmpty(name)) {
            return ResponseResult.FAILED("友情链接名称不可以为空");
        }
        //补全数据
        friendLink.setId(idWorker.nextId()+"");
        friendLink.setCreateTime(new Date());
        friendLink.setUpdateTime(new Date());
        friendLinkMapper.insertFriendLink(friendLink);
        return ResponseResult.SUCCESS("添加友情链接成功");
    }

    /**
     * 获取友情链接
     * @param friendLinkId
     * @return
     */
    @Override
    public ResponseResult getFriendLink(String friendLinkId) {
        FriendLink friendLink = friendLinkMapper.findOneById(friendLinkId);
        if (friendLink == null) {
            return  ResponseResult.FAILED("此友情链接不存在");
        }
        return ResponseResult.SUCCESS("友情链接获取成功").setData(friendLink);
    }

    /**
     * 获取友情链接集合
     * @return
     */
    @Override
    public ResponseResult listFriendLinks() {
        List<FriendLink> all = friendLinkMapper.listFriendLinkByState();
        return ResponseResult.SUCCESS("获取友情链接列表成功").setData(all);
    }

    /**
     * 删除友情链接
     * @param friendLinkId
     * @return
     */
    @Override
    public ResponseResult deleteFriendLink(String friendLinkId) {
        int result = friendLinkMapper.deleteAllById(friendLinkId);
        return result>0?ResponseResult.SUCCESS("友情链接删除成功"):ResponseResult.FAILED("该友情链接不存在");
    }

    /**
     * 更新友情链接
     * 可以更新：logo，name，url
     * @param friendLinkId
     * @param friendLink
     * @return
     */
    @Override
    public ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink) {
        FriendLink friendLinkFromDb = friendLinkMapper.findOneById(friendLinkId);
        if (friendLinkFromDb == null) {
            return ResponseResult.FAILED("友情链接更新失败");
        }
        friendLink.setId(friendLinkId);
        friendLink.setUpdateTime(new Date());
        friendLinkMapper.updateFriendLinkByConditions(friendLink);
        return ResponseResult.SUCCESS("友情链接更新成功");
    }

    /**
     * 普通用户和未登录用户获取的友情链接
     * @return
     */
    @Override
    public ResponseResult getCommonLinks() {
        List<FriendLink> friendLinks = friendLinkMapper.listFriendLinkByState();
        return ResponseResult.SUCCESS("获取友情链接成功").setData(friendLinks);
    }
}
