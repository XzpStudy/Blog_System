package net.person.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


/**
 * 线程池配置类
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean
    public Executor asyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("blog_system_task_worker-");
        //设置队列容量
        executor.setQueueCapacity(30);
        executor.initialize();
        return executor;
    }
}
