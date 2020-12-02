package net.person.blog.services;

import net.person.blog.pojo.Looper;
import net.person.blog.response.ResponseResult;

public interface ILoopService {
    ResponseResult addLoop(Looper looper);

    ResponseResult getLoop(String loopId);

    ResponseResult listLoops();

    ResponseResult updateLoop(String loopId, Looper looper);

    ResponseResult deleteLoop(String loopId);

    /**
     * 普通用户和未登录用户获取轮播图
     * @return
     */
    ResponseResult getCommonLoops();
}
