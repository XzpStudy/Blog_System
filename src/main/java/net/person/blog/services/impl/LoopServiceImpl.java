package net.person.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.LoopMapper;
import net.person.blog.pojo.Looper;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.ILoopService;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class LoopServiceImpl implements ILoopService {

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private LoopMapper loopMapper;

    /**
     * 添加轮播图
     * @param looper
     * @return
     */
    @Override
    public ResponseResult addLoop(Looper looper) {
        //检查数据
        String title = looper.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("标题不能为空");
        }
        String imageUrl = looper.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            return ResponseResult.FAILED("图片不能为空");
        }
        String targetUrl = looper.getTargetUrl();
        if (TextUtils.isEmpty(targetUrl)) {
            return ResponseResult.FAILED("跳转链接不可以为空");
        }
        //补全数据
        looper.setId(idWorker.nextId() + "");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        //保存数据
        loopMapper.insertLoop(looper);
        return ResponseResult.SUCCESS("轮播图添加成功");
    }

    /**
     * 获取单个轮播图
     * @param loopId
     * @return
     */
    @Override
    public ResponseResult getLoop(String loopId) {
        Looper loopById = loopMapper.findOneById(loopId);
        if (loopById == null) {
            return  ResponseResult.FAILED("此轮播图不存在");
        }
        return ResponseResult.SUCCESS("获取轮播图成功").setData(loopById);
    }

    /**
     * 获取轮播图集合
     * @return
     */
    @Override
    public ResponseResult listLoops() {
        List<Looper> all = loopMapper.listLoops();
        return ResponseResult.SUCCESS("获取轮播图集合成功").setData(all);
    }

    /**
     * 更新轮播图
     * @param loopId
     * @param looper
     * @return
     */
    @Override
    public ResponseResult updateLoop(String loopId, Looper looper) {
        //先查询
        Looper loopById = loopMapper.findOneById(loopId);
        if (loopById == null) {
            return ResponseResult.FAILED("此轮播图不存在，无法修改");
        }
        looper.setId(loopId);
        looper.setUpdateTime(new Date());
        //保存
        loopMapper.updateLoopByConditions(looper);
        return ResponseResult.SUCCESS("轮播图更新成功");
    }

    /**
     * 删除轮播图
     * @param loopId
     * @return
     */
    @Override
    public ResponseResult deleteLoop(String loopId) {
        loopMapper.deleteLooperByUpdateState(loopId);
        return ResponseResult.SUCCESS("轮播图删除成功");
    }

    /**
     * 普通用户和未登录用户获取轮播图
     * @return
     */
    @Override
    public ResponseResult getCommonLoops() {
        List<Looper> loops = loopMapper.listLoopsByState();
        return ResponseResult.SUCCESS("获取轮播图成功").setData(loops);
    }
}
