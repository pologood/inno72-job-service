package com.inno72.job.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class LocalUtil {
	
	public static final String getProcessID() {  
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return runtimeMXBean.getName().split("@")[0];
    } 

}
