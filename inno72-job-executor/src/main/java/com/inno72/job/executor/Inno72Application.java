package com.inno72.job.executor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.inno72.job.executor.common.Inno72JobServiceProperties;
import com.inno72.springboot.web.SpringApplicationBuilder;
import com.inno72.springboot.web.SpringBootServletInitializer;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.inno72"})
@EnableEurekaClient
@EnableConfigurationProperties({Inno72JobServiceProperties.class})
public class Inno72Application extends SpringBootServletInitializer{
	public static void main(String[] args) {
		new SpringApplicationBuilder(Inno72Application.class, "inno72-job-executor-service", args);
	}

	@Override
	public String setAppNameForLog() {
		return "inno72-job-executor-service";
	}
}