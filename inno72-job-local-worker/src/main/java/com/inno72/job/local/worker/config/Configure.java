package com.inno72.job.local.worker.config;

import org.inno72.job.example.task.TaskExample;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.inno72.job.core.handle.IJobHandler;

@Configuration
@Import({RedisAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Configure {
	
	@Configuration
    @Profile("dev")
    @PropertySource("classpath:application-dev.properties")
    static class Dev
    { 
		
    }

    @Configuration
    @Profile("test")
    @PropertySource({"classpath:application-test.properties"})
    static class Test
    {
    	
    }
    
    
    @Bean
    public IJobHandler jobHandler() {
    	return new TaskExample();
    }

}