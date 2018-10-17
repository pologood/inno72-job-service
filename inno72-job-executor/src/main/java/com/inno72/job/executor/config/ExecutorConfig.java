package com.inno72.job.executor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.util.NetUtil;
import com.inno72.job.executor.common.Inno72JobServiceProperties;

@Configuration
@EnableConfigurationProperties({ Inno72JobServiceProperties.class })
public class ExecutorConfig {
	
	
	private Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);
	
	@Bean(initMethod = "start", destroyMethod = "destroy")
    public JobExecutor jobExecutor(Inno72JobServiceProperties inno72JobServiceProperties) {
        logger.info(">>>>>>>>>>> executor-job config init.");
        
        int executorPort = NetUtil.findAvailablePort(inno72JobServiceProperties.getExecutorPort());
        JobExecutor jobExecutor = new JobExecutor();
        jobExecutor.setAdminAddresses(inno72JobServiceProperties.getAdminAddresses());
        jobExecutor.setAppName("inno72");
        jobExecutor.setIp(null);
        jobExecutor.setPort(executorPort);
        jobExecutor.setAccessToken(inno72JobServiceProperties.getAccessToken());
        jobExecutor.setLogPath("./log");
        jobExecutor.setLogRetentionDays(30);

        return jobExecutor;
    }

}
