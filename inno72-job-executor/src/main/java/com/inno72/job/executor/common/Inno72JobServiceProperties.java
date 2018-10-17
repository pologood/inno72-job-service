package com.inno72.job.executor.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.inno72.config.client.AbstractProperties;

@ConfigurationProperties(prefix = "inno72.job")
public class Inno72JobServiceProperties extends AbstractProperties{
	
	private String adminAddresses;
	
	private String accessToken;

	private int executorPort;
	

	public int getExecutorPort() {
		return executorPort;
	}

	public void setExecutorPort(int executorPort) {
		this.executorPort = executorPort;
	}

	public String getAdminAddresses() {
		return adminAddresses;
	}

	public void setAdminAddresses(String adminAddresses) {
		this.adminAddresses = adminAddresses;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	
	
}
