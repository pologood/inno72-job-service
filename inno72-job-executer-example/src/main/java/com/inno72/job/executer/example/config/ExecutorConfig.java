package com.inno72.job.executer.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.util.NetUtil;

@Configuration
public class ExecutorConfig {
	
	
	private Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);
	
	@Bean(initMethod = "start", destroyMethod = "destroy")
    public JobExecutor jobExecutor() {
        logger.info(">>>>>>>>>>> executor-job config init.");
        
        int executorPort = NetUtil.findAvailablePort(5010);
        JobExecutor jobExecutor = new JobExecutor();
        jobExecutor.setAdminAddresses("http://admin.schedule.36solo.com");
        jobExecutor.setAppName("inno72ex");
        jobExecutor.setIp(null);
        jobExecutor.setPort(executorPort);
        jobExecutor.setAccessToken("inno72_job_token");
        jobExecutor.setLogPath("./log");
        jobExecutor.setLogRetentionDays(30);

        return jobExecutor;
    }

}
