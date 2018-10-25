package com.inno72.job.core.glue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

import com.inno72.job.core.executor.JobExecutor;
import com.inno72.job.core.handle.IJobHandler;

import groovy.lang.GroovyClassLoader;


public class GlueFactory {
	private static Logger logger = LoggerFactory.getLogger(GlueFactory.class);
	
	/**
	 * groovy class loader
	 */
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

	// ----------------------------- spring support -----------------------------
	private static GlueFactory glueFactory = new GlueFactory();
	public static GlueFactory getInstance(){
		return glueFactory;
	}

	/**
	 * inject action of spring
	 * @param instance
	 */
	private void injectService(Object instance){
		if (instance==null) {
			return;
		}
	    
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			Object fieldBean = null;
			// with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired
			if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
				try {
					Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
					if (resource.name()!=null && resource.name().length()>0){
						fieldBean = JobExecutor.getApplicationContext().getBean(resource.name());
					} else {
						fieldBean = JobExecutor.getApplicationContext().getBean(field.getName());
					}
				} catch (Exception e) {
				}
				if (fieldBean==null ) {
					fieldBean = JobExecutor.getApplicationContext().getBean(field.getType());
				}
			} else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
				Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
				if (qualifier!=null && qualifier.value()!=null && qualifier.value().length()>0) {
					fieldBean = JobExecutor.getApplicationContext().getBean(qualifier.value());
				} else {
					fieldBean = JobExecutor.getApplicationContext().getBean(field.getType());
				}
			}
			
			if (fieldBean!=null) {
				field.setAccessible(true);
				try {
					field.set(instance, fieldBean);
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	// ----------------------------- load instance -----------------------------
	// load new instance, prototype
	public IJobHandler loadNewInstance(String codeSource) throws Exception{
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = groovyClassLoader.parseClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance();
				if (instance!=null) {
					if (instance instanceof IJobHandler) {
						this.injectService(instance);
						return (IJobHandler) instance;
					} else {
						throw new IllegalArgumentException(">>>>>>>>>>> glue, loadNewInstance error, "
								+ "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
					}
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> glue, loadNewInstance error, instance is null");
	}

}
