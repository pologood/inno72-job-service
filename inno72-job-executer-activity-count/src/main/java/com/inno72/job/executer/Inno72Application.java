package com.inno72.job.executer;

import com.inno72.job.executer.config.Inno72GameServiceProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.inno72.springboot.web.SpringApplicationBuilder;
import com.inno72.springboot.web.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.inno72"})
@EnableFeignClients
@EnableEurekaClient
@EnableCircuitBreaker // 开启熔断
@EnableWebMvc
@EnableConfigurationProperties({Inno72GameServiceProperties.class})
public class Inno72Application extends SpringBootServletInitializer
{
	public static void main(String[] args) {
		new SpringApplicationBuilder(Inno72Application.class, "inno72-job-executor-activity-count", args);
	}

	@Override
	public String setAppNameForLog() {
		return "inno72-job-executor-activity-count";
	}
}
