package com.inno72.job.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobFileAppender;



@Component
public class ExeJarManager {
	
	private Map<Integer, ExeJarInfoBean> exeJarInfoMap = new ConcurrentHashMap<Integer, ExeJarInfoBean>();
	
	private Pattern classPattern = Pattern.compile("([^\\$]*)\\.class$");
	
	private static final Logger logger = LoggerFactory.getLogger(ExeJarManager.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	class ExeJarInfoBean{
		DynamicJarClassLoader loader;
		int jobId;
		long version;
		IJobHandler handler;
		
		public ExeJarInfoBean(int jobId, long version, URL jarUrl) {
			this.loader = new DynamicJarClassLoader(new URL[]{jarUrl}, ExeJarManager.class.getClassLoader());
			this.jobId = jobId;
			this.version = version;
		}
		
	}
	
	public IJobHandler loadJar(int jobId, String handle, long version, File jarFile) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		ExeJarInfoBean exeJarInfo = new ExeJarInfoBean(jobId, version, jarFile.toURI().toURL());
		
		List<String> candidataClasses = isCandidateClass(jarFile);
		
		IJobHandler ret = null;
		try {
			for(String candidataClass : candidataClasses ) {
				logger.info("loading class:" + candidataClass);
				Class<?> clazz = exeJarInfo.loader.loadClass(candidataClass, true);
				JobHandler annoJobHandle = clazz.getAnnotation(JobHandler.class);
				if(annoJobHandle != null && isImplementsHandle(clazz)) {
					if(annoJobHandle.value() != null && annoJobHandle.value().equals(handle)){
						
						ret = (IJobHandler) clazz.newInstance();
						Field[] fields = clazz.getDeclaredFields();
						for(Field field : fields) {
							Resource resource = field.getAnnotation(Resource.class);
							if(resource != null) {
								field.setAccessible(true);
								if(!"".equals(resource.name())) {
									field.set(ret, applicationContext.getBean(resource.name()));
								}else {
									field.set(ret, applicationContext.getBean(field.getType()));
								}
							}
						}
					}
				}
			}
		}catch(Exception e) {
			ret = null;
			exeJarInfo.loader.close();
			throw e;
		}
		
		if(ret == null) {
			exeJarInfo.loader.close();
		}else {
			exeJarInfo.handler = ret;
			exeJarInfoMap.put(jobId, exeJarInfo);
		}
		
		return ret;
	}
	
	public void unloadJar(int jobId) throws IOException {
		
		ExeJarInfoBean bean = exeJarInfoMap.remove(jobId);
		if(bean != null) {
			bean.handler = null;
			bean.loader.close();
		}
	
	}
	
	protected boolean isImplementsHandle(Class<?> clazz) {
		if (IJobHandler.class.isAssignableFrom(clazz)) {
			return true;
		}else {
			return false;
		}
    	
	}
	
	protected List<String> isCandidateClass(File jarFile) throws IOException {
		
		List<String> classPaths = new LinkedList<String>();
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entrys = jar.entries();
		while (entrys.hasMoreElements()) {
			JarEntry jarEntry = entrys.nextElement();
			classPaths.add(jarEntry.getName());	
		}
		
		List<String> candidataClasses = new LinkedList<String>();
		for(String clazzPath : classPaths) {
			if(classPattern.matcher(clazzPath).matches()) {
				clazzPath = clazzPath.replace(".class", "");
				clazzPath = clazzPath.replace("/", ".");
				candidataClasses.add(clazzPath);
			}
		}
		jar.close();
		return candidataClasses;
		
	}
	
	public void saveJarFile(File jarFile, byte[] jar) throws IOException {
        
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(jarFile);
            fileOutputStream.write(jar);
            fileOutputStream.close();
        } catch (Exception e) {
            throw e;
        }finally{
            if(fileOutputStream != null){
                fileOutputStream.close();
            }
        }
    }
	
	public String getJarFileName(int jobId, long glueUpdatetime) {
		
		return JobFileAppender.getGlueSrcPath()
                .concat("/")
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(".jar");
		
	}

	

}
