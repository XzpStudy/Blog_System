package net.person.blog;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.person.blog.utils.RedisUtil;
import net.person.blog.utils.SnowflakeIdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;
import java.util.Random;

/**
 * SpringBoot项目启动类
 * @author 16272
 */
@Slf4j
@EnableSwagger2
@MapperScan(value = "net.person.blog.dao")
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        log.info("SpringBoot项目已经启动");
        SpringApplication.run(BlogApplication.class,args);
    }

    @Bean
    public SnowflakeIdWorker createIdWorker(){
        return new SnowflakeIdWorker(0,0);
    }

    @Bean
    public BCryptPasswordEncoder createPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisUtil createRedisUtil(){
        return new RedisUtil();
    }

    @Bean
    public Random createRandom(){
        return new Random();
    }

    @Bean
    public Gson createGson(){
        return new Gson();
    }

    /**
     * 上传文件大小设置
     * @return
     */
    @Bean
    public MultipartConfigElement createMultipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //设置上传文件大小单个最大值
        factory.setMaxFileSize("50MB");
        //设置总上传数据大小
        factory.setMaxRequestSize("100MB");
        return factory.createMultipartConfig();
    }

}
