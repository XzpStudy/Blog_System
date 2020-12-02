package net.person.blog.controller.admin;


import net.person.blog.pojo.Looper;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.ILoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 轮播图的相关接口
 */
@RestController
@RequestMapping("/admin/looper")
public class LooperAdminApi {


    @Autowired
    private ILoopService looperService;

    /**
     * 添加轮播图
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper){
        return looperService.addLoop(looper);
    }

    /**
     * 删除轮播图
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{loopId}")
    public ResponseResult deleteLoop(@PathVariable("loopId")String loopId){
        return looperService.deleteLoop(loopId);
    }

    /**
     * 修改轮播图
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{loopId}")
    public ResponseResult updateLoop(@PathVariable("loopId")String loopId,@RequestBody Looper looper){
        return looperService.updateLoop(loopId,looper);
    }

    /**
     * 获取轮播图
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{loopId}")
    public ResponseResult getLoop(@PathVariable("loopId")String loopId){
        return looperService.getLoop(loopId);
    }

    /**
     * 获取轮播图集合
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listLoops(){
        return looperService.listLoops();
    }
}
