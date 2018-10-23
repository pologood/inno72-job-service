package com.inno72.job.core.handle;

import com.inno72.job.core.biz.model.ReturnT;

public interface IJobHandler {


	/**
	 * execute handler, invoked when executor receives a scheduling request
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String param) throws Exception;


	/**
	 * init handler, invoked when JobThread init
	 */
	public void init();

	/**
	 * destroy handler, invoked when JobThread destroy
	 */
	public void destroy();


}
