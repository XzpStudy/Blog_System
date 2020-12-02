package net.person.blog.dao;

import net.person.blog.pojo.FriendLink;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface FriendLinkMapper {

    FriendLink findOneById(String id);

    int deleteAllById(String friendLinkId);

    List<FriendLink> listFriendLinkByState();

    int insertFriendLink(FriendLink friendLink);

    int updateFriendLinkByConditions(FriendLink friendLink);
}
