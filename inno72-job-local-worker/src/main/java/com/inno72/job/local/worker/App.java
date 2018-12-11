package com.inno72.job.local.worker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.inno72.job.core.biz.model.ReturnT;
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

		IJobHandler handler = (IJobHandler) context.getBean("testTask");
		if (handler != null) {
			handler.init();
			ReturnT<String> ret = handler.execute("2018-11-21");
			System.out.println(ret.toString());
			handler.destroy();
		}
		context.close();

	}


}
