package com.inno72.job.local.worker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.local.worker.config.Configure;

/**
 * Hello world!
 *
 */
public class App {


	public static void main(String[] args) throws Exception {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("dev");
		context.register(Configure.class);
		context.scan("com.inno72");
		context.refresh();

		IJobHandler handler = context.getBean(IJobHandler.class);
		if (handler != null) {
			handler.init();
			handler.execute("");
			handler.destroy();
		}
		context.close();

	}


}
