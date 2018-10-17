package com.inno72.job.core.biz.model;

public class JarResponse {
	
	private byte[] jarFile;
	
	private String checksum;

	public byte[] getJarFile() {
		return jarFile;
	}

	public void setJarFile(byte[] jarFile) {
		this.jarFile = jarFile;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	

}
