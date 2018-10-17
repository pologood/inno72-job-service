package com.inno72.job.admin.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.inno72.job.admin.common.Inno72JobServiceProperties;
import com.inno72.job.admin.scheduler.JobDynamicScheduler;


@Configuration
@EnableConfigurationProperties({ Inno72JobServiceProperties.class })
public class SchedulerConfig {
	
	@Lazy(false)
	@Bean
	SchedulerFactoryBean quartzScheduler(DataSource dataSource) {

		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		schedulerFactoryBean.setDataSource(dataSource);
		schedulerFactoryBean.setAutoStartup(true);
		schedulerFactoryBean.setStartupDelay(20);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
		schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));

		return schedulerFactoryBean;

	}
	
	
	@Bean(initMethod = "init", destroyMethod = "destroy")
	JobDynamicScheduler jobDynamicScheduler(SchedulerFactoryBean quartzScheduler, Inno72JobServiceProperties inno72JobServiceProperties) {
		
		JobDynamicScheduler jobDynamicScheduler = new JobDynamicScheduler();
		jobDynamicScheduler.setScheduler(quartzScheduler.getObject());
		jobDynamicScheduler.setAccessToken(inno72JobServiceProperties.getAccessToken());
		
		return jobDynamicScheduler;
		
	}


}
