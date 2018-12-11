package com.inno72.job.local.worker.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.inno72.job.core.handle.annotation.JobMapperScanner;

public class MybatisCondition implements Condition{

	
	
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		JobMapperScanner jobMapperScanner = Configure.TaskClazz.getAnnotation(JobMapperScanner.class);
		if(jobMapperScanner == null) return false;
		return true;
	}
	
	

}
