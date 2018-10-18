package com.inno72.job.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.service.JobService;
import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.rpc.codec.RpcRequest;
import com.inno72.job.core.rpc.codec.RpcResponse;
import com.inno72.job.core.rpc.netcom.NetComServerFactory;
import com.inno72.job.core.rpc.serialize.HessianSerializer;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.HttpClientUtil;
import com.inno72.job.core.util.Submittable;

@Controller
public class JobApiController {
    private static Logger logger = LoggerFactory.getLogger(JobApiController.class);

    @Resource
	private JobService jobService;
    
    private RpcResponse doInvoke(HttpServletRequest request) {
        try {
        	
            byte[] requestBytes = HttpClientUtil.readBytes(request);
            if (requestBytes == null || requestBytes.length==0) {
                RpcResponse rpcResponse = new RpcResponse();
                rpcResponse.setError("RpcRequest byte[] is null");
                return rpcResponse;
            }
            RpcRequest rpcRequest = (RpcRequest) HessianSerializer.deserialize(requestBytes, RpcRequest.class);

            // invoke
            RpcResponse rpcResponse = NetComServerFactory.invokeService(rpcRequest, null);
            return rpcResponse;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setError("Server-error:" + e.getMessage());
            return rpcResponse;
        }
    }

    @RequestMapping(AdminBiz.MAPPING)
    public void api(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // invoke
        RpcResponse rpcResponse = doInvoke(request);

        // serialize response
        byte[] responseBytes = HessianSerializer.serialize(rpcResponse);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        OutputStream out = response.getOutputStream();
        out.write(responseBytes);
        out.flush();
    }
    
    @RequestMapping("/api/download/jar")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	    String jodId = request.getParameter("jodId");
	    if(jodId == null) {
	    	response.sendError(400, "miss jodId");
	    	return;
	    }
	    	
	    JobInfo jobInfo = jobService.findJobInfo(Integer.valueOf(jodId));
	    
	    if(jobInfo == null) {
	    	response.sendError(400, "jodId not found");
	    	return;
	    }
	    	
	    String prefix = jobInfo.getGlueSource();
		final String jarFileName = String.format("./jar_repositories/%s.jar", prefix);
		final String jarLockFileName = String.format("./jar_repositories_lock/%s.lock", prefix);
	    	
	    	
		byte[] jarFile = (byte[]) FileUtil.processByFileLock(new File(jarLockFileName), new Submittable() {
			@Override
			public Object submit() throws IOException {

				File file = new File(jarFileName);
				Long filelength = file.length();
				byte[] jarFile = new byte[filelength.intValue()];

				FileInputStream in = new FileInputStream(file);
				in.read(jarFile);
				in.close();

				return jarFile;
			}
		});
		
		response.setContentType("application/octet-stream");
        response.setStatus(HttpServletResponse.SC_OK);

        OutputStream out = response.getOutputStream();
        out.write(jarFile);
        out.flush();
    }

}
