package com.inno72.job.local.worker.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import com.inno72.job.task.task.UserProfileIntervalTimeTask;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;

@Configuration
@Import({RedisAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Configure {

	static final public Class<? extends IJobHandler> TaskClazz = UserProfileIntervalTimeTask.class;

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
	@Conditional(MybatisCondition.class)
	public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws IOException {

		JobMapperScanner jobMapperScanner = TaskClazz.getAnnotation(JobMapperScanner.class);
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

		factory.setDataSource(dataSource);

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		List<org.springframework.core.io.Resource> resourceList = new LinkedList<org.springframework.core.io.Resource>();

		for(String value : jobMapperScanner.value()) {
			org.springframework.core.io.Resource[] res =  resolver.getResources(value);
			if(res != null && res.length > 0)
				resourceList.addAll(Arrays.asList(res));
		}

		try {

			org.springframework.core.io.Resource[] res = new org.springframework.core.io.Resource[resourceList.size()];
			factory.setMapperLocations(resourceList.toArray(res));
			return factory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Bean
	@Conditional(MybatisCondition.class)
	public MapperScannerConfigurer mapperScannerConfigurer() {
		JobMapperScanner jobMapperScanner = TaskClazz.getAnnotation(JobMapperScanner.class);
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
		mapperScannerConfigurer.setBasePackage(jobMapperScanner.basePackage());
		return mapperScannerConfigurer;
	}


	@Bean(name="testTask")
	public IJobHandler jobHandler() throws Exception {
		return TaskClazz.newInstance();
	}

}
