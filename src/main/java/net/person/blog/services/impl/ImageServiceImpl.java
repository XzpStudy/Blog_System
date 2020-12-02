package net.person.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.person.blog.dao.ImageMapper;
import net.person.blog.pojo.BlogUser;
import net.person.blog.pojo.Image;
import net.person.blog.response.ResponseResult;
import net.person.blog.services.IImageService;
import net.person.blog.services.IUserService;
import net.person.blog.utils.Constants;
import net.person.blog.utils.SnowflakeIdWorker;
import net.person.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements IImageService {

    @Value("${blog.system.image.save-path}")
    public String path;

    @Value("${blog.system.image.max-size}")
    public long maxSize;

    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private IUserService userService;

    @Autowired
    private ImageMapper imageMapper;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    /**
     * 图片上传
     * 目录创建规则：配置目录/日期/图片类型/ID.类型
     * 返回数据中图片命名规则：时间戳_图片id.type
     * 图片命名 ----》 id，每天一个文件夹保存
     * 限制文件大小
     * 保存记录到数据库中
     *
     * @param file
     * @return
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        //判断是否有文件
        if (file == null) {
            return ResponseResult.FAILED("图片不可以为空");
        }
        //判断文件类型,只支持文件上传
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("图片格式错误");
        }
        //判断图片格式
        log.info("uploadImage  =====>  contentType = " + contentType);
        String type = getType(contentType);
        if (type == null) {
            return ResponseResult.FAILED("不支持此种图片格式");
        }
        //获取图片信息
        String originalFilename = file.getOriginalFilename();
        //判断图片大小
        long size = file.getSize();
        log.info("uploadImage ====> size = "+size);
        if (size > maxSize) {
            return ResponseResult.FAILED("图片最大只支持" + (maxSize / 1024 / 1024) + "M");
        }
        //创建图片保存目录
        //目录创建规则：配置目录/日期/图片类型/ID.类型
        long currentTimeMillis = System.currentTimeMillis();
        String currentDay = simpleDateFormat.format(currentTimeMillis);
        //创建日期文件夹
        String dayPath = path + File.separator + currentDay;
        //判断日期文件夹是否存在
        File dayPathFile = new File(dayPath);
        if (!dayPathFile.exists()) {
            dayPathFile.mkdirs();
        }
        //获取图片ID
        String id = String.valueOf(idWorker.nextId());
        String targetPath = dayPath + File.separator +
                type + File.separator + id + "." + type;
        File targetFile = new File(targetPath);
        //类型文件夹不存在就创建
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        //保存图片
        try {
            //文件不存在就创建
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
            //返回结果，包含图片的名称和url
            Map<String, String> result = new HashMap<>();
            //存储的路径
            //返回数据中图片命名规则：时间戳_图片id.type
            String resultPath = currentTimeMillis + "_" + id + "." + type;
            result.put("id", resultPath);
            result.put("name", originalFilename);

            //后期优化，可以增加MD5值查询验证，达到去重效果
            //保存记录到数据库
            Image image = new Image();
            image.setContentType(type);
            image.setId(id);
            image.setCreateTime(new Date());
            image.setUpdateTime(new Date());
            //服务器或电脑中的存储路径
            image.setPath(targetFile.getPath());
            image.setName(originalFilename);
            image.setState("1");
            BlogUser blogUser = userService.checkBlogUser();
            if(blogUser == null){
                return ResponseResult.FAILED("用户登录已过期，请重新登录");
            }
            image.setUserId(blogUser.getId());
            //浏览器访问路径
            image.setUrl(resultPath);
            imageMapper.insertImageMessage(image);
            return ResponseResult.SUCCESS("图片上传成功").setData(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("图片上传失败，请稍后重试");
    }

    /**
     * 图片格式验证
     * @param contentType
     * @return
     */
    private String getType(String contentType) {
        String type = null;
        if (Constants.ImageType.TYPE_JPG_WITH_PREFIX.equals(contentType)) {
            type = Constants.ImageType.TYPE_JPG;
        } else if (Constants.ImageType.TYPE_PNG_WITH_PREFIX.equals(contentType)) {
            type = Constants.ImageType.TYPE_PNG;
        } else if (Constants.ImageType.TYPE_GIF_WITH_PREFIX.equals(contentType)) {
            type = Constants.ImageType.TYPE_GIF;
        }
        return type;
    }

    private String getPrefixType(String type) {
        if (Constants.ImageType.TYPE_JPG.equals(type)) {
            type = Constants.ImageType.TYPE_JPG_WITH_PREFIX;
        } else if (Constants.ImageType.TYPE_PNG.equals(type)) {
            type = Constants.ImageType.TYPE_PNG_WITH_PREFIX;
        } else if (Constants.ImageType.TYPE_GIF.equals(type)) {
            type = Constants.ImageType.TYPE_GIF_WITH_PREFIX;
        }
        return type;
    }

    /**
     * 获取图片
     * 返回数据中图片命名规则：时间戳_图片id.type
     * 拿到图片ID，查询state状态
     * @param response
     * @param imageId
     * @return
     */
    @Override
    public void viewImage(HttpServletResponse response, String imageId) throws IOException {
        //配置目录已知,需要知道类型文件夹,需要知道日期文件夹,ID
        //根据尺寸动态的将图片返回给前端
        //好处：节省带宽，传输快
        //坏处：后台cpu资源占用高
        //推荐做法：图片上传时分别复制三份，大、中、小
        //根据尺寸返回结果
        String[] paths = imageId.split("_");
        String dayPath = paths[0];
        String name = paths[1];
        String type = paths[1].split("\\.")[1];
        String imageID = paths[1].split("\\.")[0];
        Image imageOneById = imageMapper.findOneById(imageID);
        if (imageOneById == null) {
            return;
        }
        if(!"1".equals(imageOneById.getState())){
            return;
        }
        log.info("viewImage   ===>  type = " + type);
        String targetPath = path + File.separator + simpleDateFormat.format(Long.parseLong(dayPath)) + File.separator +
                type + File.separator + name;
        log.info("viewImage   ===>  targetPath = " + targetPath);
        File file = new File(targetPath);
        OutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            String contentType = getPrefixType(type);
            log.info("viewImage ====>  contentType = "+contentType);
            response.setContentType(contentType);
            outputStream = response.getOutputStream();
            //读取图片
            inputStream = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 获取图片列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult listImages(int page, int size) {
        BlogUser blogUser = userService.checkBlogUser();
        if(blogUser == null){
            return ResponseResult.FAILED("用户登录已过期，请重新登录");
        }
        String userId = blogUser.getId();
        page = Math.max(Constants.Page.DEFAULT_PAGE,page);
        size = Math.max(Constants.Page.DEFAULT_SIZE,size);
        List<Image> all = imageMapper.getImagesByConditions((page - 1) * size, size, userId, "1");
        return ResponseResult.SUCCESS("获取图片列表成功").setData(all);
    }

    /**
     * 删除图片（就是修改状态）
     * @param imageId
     * @return
     */
    @Override
    public ResponseResult deleteImage(String imageId) {
        int result = imageMapper.deleteImageByUpdateState(imageId);
        return result>0?ResponseResult.SUCCESS("删除图片成功"):ResponseResult.FAILED("删除失败，图片不存在");
    }
}
