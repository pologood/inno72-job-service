package com.inno72.job.executer.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.core.util.HttpFormConnector;
import com.inno72.job.executer.mapper.OrderMapper;
import com.inno72.job.executer.model.OrderModel;
import com.inno72.job.executer.util.FastJsonUtils;


@Component
@JobHandler("game.fixOrderStatusTask")
public class FixOrderStatusTask implements IJobHandler{

	private final static String jstUrlDev="http://inno72test.ews.m.jaeapp.com/";
	private final static String jstUrlTest="http://inno72test.ews.m.jaeapp.com/";
	private final static String jstUrlProd="https://inno72top.ews.m.jaeapp.com/";
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ApplicationContext context;
	
	@Resource(name = "orderFixExecutorService")
	private ExecutorService executorService;
	
	@Resource
	private OrderMapper orderMapper;
	
	
	class FixOrderStatusTaskJob implements Runnable {

		public String orderId;
		
		public String refOrderId;
		
		public String accessToken;
		
		public FixOrderStatusTaskJob(String orderId, String refOrderId, String accessToken) {
			this.orderId = orderId;
			this.refOrderId = refOrderId;
			this.accessToken = accessToken;
		}
		
		@Override
		public void run() {
			
			String jstUrl;
			try {
				
				jstUrl = getJstUrl();
				
				Map<String, String> form = new HashMap<String, String>();
				
				form.put("orderId", refOrderId);
				form.put("accessToken", accessToken);
				
				byte[] ret = HttpFormConnector.doPost(jstUrl+"/api/top/order-polling/", form, 3000);
				String retStr = new String(ret, "utf-8");
				
				logger.info("FixOrderStatusTaskJob orderId jst rep:" + retStr);
				
				if (StringUtil.isEmpty(retStr)) {
					JobLogger.log("orderId:"+refOrderId+" ret is empty");
					return;
				}
			
				String msg_code = FastJsonUtils.getString(retStr, "msg_code");
				
				if (!msg_code.equals("SUCCESS")) {
					JobLogger.log("orderId:"+refOrderId+" return fail");
					return;
				}
				
				boolean model = Boolean.valueOf(FastJsonUtils.getString(retStr, "model"));
				
				if(model) {
					orderMapper.updateUnpayOrderStatus(this.orderId, 1, new Date());				
				}
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				JobLogger.log("orderId:"+refOrderId+" net fail:" + e.getMessage());
			}		
		}
		
	}
	
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		
		long pastTime = Long.parseLong(param);
		
		long queryTime = System.currentTimeMillis() - pastTime*1000*60;
		
		List<OrderModel> orderModelList =  orderMapper.queryUnpayOrder(new Date(queryTime));
		
		List<Future<?>> futureList = new ArrayList<Future<?>>();
		
		for(OrderModel model : orderModelList) {
			
			if(StringUtils.isBlank(model.getAccessToken())) {
				continue;
			}
			
			Future<?> f = executorService.submit(new FixOrderStatusTaskJob(model.getId(), model.getRefOrderId(), model.getAccessToken()));
			futureList.add(f);
		}
		
		if(futureList.size() > 0) {
			while(true) {
				boolean isOk = true;
				for(Future<?> f : futureList) {
					if(!f.isDone()) {
						isOk = false;
						break;
					}
				}
				
				if(isOk) break;
				else Thread.sleep(1000);
			}
		}
		return new ReturnT<String>(ReturnT.SUCCESS_CODE, "处理"+orderModelList.size()+"条");
	}
	
	private String getJstUrl() throws IOException {
		
		String state = context.getEnvironment().getActiveProfiles()[0];
		
		switch(state) {
			
			case "dev": return jstUrlDev;
			case "test": return jstUrlTest;
			case "stage":
			case "prod": return jstUrlProd;
			default:
				throw new IOException("未找到jsturl:" + state);
			
		}
		
	}
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}