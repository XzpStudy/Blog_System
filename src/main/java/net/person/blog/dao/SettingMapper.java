package net.person.blog.dao;

import net.person.blog.pojo.Setting;
import org.springframework.stereotype.Component;

@Component
public interface SettingMapper {

    public Setting findOneByKey(String key);

    public int insertOneSetting(Setting setting);

    public int updateSetting(Setting setting);
}
