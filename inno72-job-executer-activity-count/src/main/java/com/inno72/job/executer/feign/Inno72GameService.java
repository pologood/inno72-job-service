package com.inno72.job.executer.feign;

import com.inno72.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "inno72GameService")
public interface Inno72GameService {

	@RequestMapping(value = "/newretail/findStores", method = RequestMethod.POST)
	Result<Object> findStores(@RequestParam("storeName") String storeName);
}
