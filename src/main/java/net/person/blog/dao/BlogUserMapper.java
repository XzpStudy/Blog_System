package net.person.blog.dao;

import net.person.blog.pojo.BlogUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlogUserMapper {

    public BlogUser findOneByUserName(String userName);

    public BlogUser findOneByEmail(String email);

    public BlogUser findOneById(String userId);

    /**
     * 更新用户状态为0表示删除
     */
    public int deleteUserByState(String userId);

    public int updatePasswordByEmail(@Param("encode") String encode,@Param("email") String email);

    public int updateEmailById(@Param("email")String email,@Param("id") String id);

    public int insertOneUser(BlogUser blogUser);

    public int updateUserByCondition(BlogUser blogUser);

    public List<BlogUser> getUserByPage(@Param("startIndex") int startIndex,@Param("size") int size);
}
