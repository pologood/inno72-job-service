package com.inno72.job.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobFileAppender;



@Component
public class ExeJarManager {
	
	private Map<Integer, ExeJarInfoBean> exeJarInfoMap = new ConcurrentHashMap<Integer, ExeJarInfoBean>();
	
	private Pattern classPattern = Pattern.compile("([^\\$]*)\\.class$");
	
	private static final Logger logger = LoggerFactory.getLogger(ExeJarManager.class);
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	private DataSource dataSource;
	
	class ExeJarInfoBean{
		private DynamicJarClassLoader loader;
		private int jobId;
		private long version;
		private IJobHandler handler;
		private SqlSessionTemplate sqlSession = null;
		
		public ExeJarInfoBean(int jobId, long version, URL jarUrl) {
			this.loader = new DynamicJarClassLoader(new URL[]{jarUrl}, ExeJarManager.class.getClassLoader());
			this.jobId = jobId;
			this.version = version;
		}

		public DynamicJarClassLoader getLoader() {
			return loader;
		}

		public void setLoader(DynamicJarClassLoader loader) {
			this.loader = loader;
		}

		public int getJobId() {
			return jobId;
		}

		public void setJobId(int jobId) {
			this.jobId = jobId;
		}

		public long getVersion() {
			return version;
		}

		public void setVersion(long version) {
			this.version = version;
		}

		public IJobHandler getHandler() {
			return handler;
		}

		public void setHandler(IJobHandler handler) {
			this.handler = handler;
		}

		public SqlSessionTemplate getSqlSession() {
			return sqlSession;
		}

		public void setSqlSession(SqlSessionTemplate sqlSession) {
			this.sqlSession = sqlSession;
		}
	}
	
	public IJobHandler loadJar(int jobId, String handle, long version, File jarFile) throws Exception {
		
		ExeJarInfoBean exeJarInfo = new ExeJarInfoBean(jobId, version, jarFile.toURI().toURL());
		
		List<String> candidataClasses = isCandidateClass(jarFile);
		
		IJobHandler jobHandler = null;
		try {
			for(String candidataClass : candidataClasses ) {
				logger.info("loading class:" + candidataClass);
				Class<?> clazz = exeJarInfo.loader.loadClass(candidataClass, true);
				JobHandler annoJobHandle = clazz.getAnnotation(JobHandler.class);
				if(annoJobHandle != null && isImplementsHandle(clazz)) {
					if(annoJobHandle.value() != null && annoJobHandle.value().equals(handle)){
						
						JobMapperScanner mapperScan = clazz.getAnnotation(JobMapperScanner.class);
						if(mapperScan != null && mapperScan.value() != null && mapperScan.value().length > 0) {
							
							ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(new DefaultResourceLoader(exeJarInfo.loader));
							List<org.springframework.core.io.Resource> resourceList = new LinkedList<org.springframework.core.io.Resource>();
							
							for(String value : mapperScan.value()) {
								org.springframework.core.io.Resource[] res =  resolver.getResources(value);
								if(res != null && res.length > 0)
									resourceList.addAll(Arrays.asList(res));
							}
							resolver = null;
							org.springframework.core.io.Resource[] resArray = new org.springframework.core.io.Resource[resourceList.size()];
							Resources.setDefaultClassLoader(exeJarInfo.loader);
							SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
							sqlSessionFactoryBean.setMapperLocations(resourceList.toArray(resArray));
							sqlSessionFactoryBean.setDataSource(dataSource);
							
							SqlSessionTemplate sqlSession = new SqlSessionTemplate(sqlSessionFactoryBean.getObject());
							Resources.setDefaultClassLoader(ClassLoader.getSystemClassLoader());
							exeJarInfo.setSqlSession(sqlSession);
						}
						
						jobHandler = (IJobHandler) clazz.newInstance();
						Field[] fields = clazz.getDeclaredFields();
						for(Field field : fields) {
							Resource resource = field.getAnnotation(Resource.class);
							if(resource != null) {
								
								try {
									field.setAccessible(true);
									if(StringUtils.isNotBlank(resource.name())) {
										field.set(jobHandler, applicationContext.getBean(resource.name()));
									}else {
										field.set(jobHandler, applicationContext.getBean(field.getType()));
									}
								} catch(BeansException ex) {
									if(exeJarInfo.getSqlSession() != null) {
										if(exeJarInfo.getSqlSession().getConfiguration().hasMapper(field.getType())) {
											field.set(jobHandler, exeJarInfo.getSqlSession().getMapper(field.getType()));
										}else {
											throw ex;
										}
									}else {
										throw ex;
									}
								} finally {
									field.setAccessible(false);
								}
							}
							
							Autowired autowired = field.getAnnotation(Autowired.class);
							if(autowired != null) {
								try {
									field.setAccessible(true);
									field.set(jobHandler, applicationContext.getBean(field.getType()));
								} catch(BeansException ex) {
									if(exeJarInfo.getSqlSession() != null) {
										if(exeJarInfo.getSqlSession().getConfiguration().hasMapper(field.getType())) {
											field.set(jobHandler, exeJarInfo.getSqlSession().getMapper(field.getType()));
										}else {
											throw ex;
										}
									}else {
										throw ex;
									}
								} finally {
									field.setAccessible(false);
								}
							}
							
						}
					}
				}
			}
		}catch(Exception e) {
			jobHandler= null;
			exeJarInfo.loader.close();
			throw e;
		}
		
		if(jobHandler == null) {
			exeJarInfo.loader.close();
		}else {
			exeJarInfo.handler = jobHandler;
			exeJarInfoMap.put(jobId, exeJarInfo);
		}
		
		return jobHandler;
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
