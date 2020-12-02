package net.person.blog.dao;

import net.person.blog.pojo.Looper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LoopMapper {

    Looper findOneById(String loopId);

    List<Looper> listLoopsByState();

    int insertLoop(Looper looper);

    List<Looper> listLoops();

    int updateLoopByConditions(Looper looper);

    int deleteLooperByUpdateState(String loopId);
}
