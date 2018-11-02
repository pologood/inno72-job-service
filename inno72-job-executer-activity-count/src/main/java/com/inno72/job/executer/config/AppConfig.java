package com.inno72.job.executer.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	
	@Bean(name="orderFixExecutorService")
	public ExecutorService getOrderFixThreadPool() {
		
		return new ThreadPoolExecutor(10, 50,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("FixOrder_" + thread.getId());  //对新创建的线程做一些操作
                        return thread;
                    }
                });
		
		
	}
	
}
