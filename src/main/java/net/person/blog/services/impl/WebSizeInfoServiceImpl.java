package net.person.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.SettingMapper;
import net.person.blog.pojo.Setting;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IWebSizeInfoService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class WebSizeInfoServiceImpl implements IWebSizeInfoService {


    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Override
    public ResponseResult getWebSizeTitle() {
        Setting titleByKey = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_TITLE);
        return ResponseResult.SUCCESS("网站title获取成功").setData(titleByKey);
    }

    @Override
    public ResponseResult putWebSizeTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("title内容不能为空");
        }
        boolean flag = false;
        Setting titleFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_TITLE);
        if (titleFromDb == null) {
            titleFromDb = new Setting();
            titleFromDb.setId(idWorker.nextId()+"");
            titleFromDb.setKey(Constants.Settings.WEB_SIZE_TITLE);
            titleFromDb.setCreateTime(new Date());
            flag = true;
        }
        titleFromDb.setValue(title);
        titleFromDb.setUpdateTime(new Date());
        if (flag){
            //插入新数据
            settingMapper.insertOneSetting(titleFromDb);
        }else {
            settingMapper.updateSetting(titleFromDb);
        }
        return ResponseResult.SUCCESS("网站title更新成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        Setting descriptionFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
        Setting keyWordsFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_KEYWORDS);
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put(descriptionFromDb.getKey(),descriptionFromDb.getValue());
        resultMap.put(keyWordsFromDb.getKey(),keyWordsFromDb.getValue());
        return ResponseResult.SUCCESS("获取SEO信息成功").setData(resultMap);
    }

    @Override
    public ResponseResult putSeoInfo(String keywords, String description) {
        if (TextUtils.isEmpty(keywords) || TextUtils.isEmpty(description)) {
            return ResponseResult.FAILED("关键字或者描述不能为空");
        }
        boolean flagKey= false;
        boolean flagDescription = false;
        Setting keyWordsFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_KEYWORDS);
        if (keyWordsFromDb == null) {
            keyWordsFromDb = new Setting();
            keyWordsFromDb.setId(idWorker.nextId()+"");
            keyWordsFromDb.setKey(Constants.Settings.WEB_SIZE_KEYWORDS);
            keyWordsFromDb.setCreateTime(new Date());
            flagKey = true;
        }
        keyWordsFromDb.setValue(keywords);
        keyWordsFromDb.setUpdateTime(new Date());

        Setting descriptionFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
        if (descriptionFromDb == null) {
            descriptionFromDb = new Setting();
            descriptionFromDb.setId(idWorker.nextId()+"");
            descriptionFromDb.setKey(Constants.Settings.WEB_SIZE_DESCRIPTION);
            descriptionFromDb.setCreateTime(new Date());
            flagDescription = true;
        }
        descriptionFromDb.setValue(keywords);
        descriptionFromDb.setUpdateTime(new Date());
        if(flagKey){
            settingMapper.insertOneSetting(keyWordsFromDb);
        }else {
            settingMapper.updateSetting(keyWordsFromDb);
        }
        if(flagDescription){
            settingMapper.insertOneSetting(descriptionFromDb);
        }else {
            settingMapper.updateSetting(descriptionFromDb);
        }
        return ResponseResult.SUCCESS("更新SEO信息成功");
    }

    @Override
    public ResponseResult getSizeViewCount() {
        Setting viewCountFromDb = settingMapper.findOneByKey(Constants.Settings.WEB_SIZE_VIEW_COUNT);
        if (viewCountFromDb == null) {
            viewCountFromDb = new Setting();
            viewCountFromDb.setId(idWorker.nextId()+"");
            viewCountFromDb.setKey(Constants.Settings.WEB_SIZE_VIEW_COUNT);
            viewCountFromDb.setCreateTime(new Date());
            viewCountFromDb.setValue("1");
            viewCountFromDb.setUpdateTime(new Date());
        }
        Map<String,Integer> resultMap = new HashMap<>();
        resultMap.put(viewCountFromDb.getKey(),Integer.parseInt(viewCountFromDb.getValue()));
        return ResponseResult.SUCCESS("获取网站浏览量成功").setData(viewCountFromDb);
    }
}
