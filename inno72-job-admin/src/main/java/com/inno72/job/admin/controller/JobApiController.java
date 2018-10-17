package com.inno72.job.admin.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.rpc.codec.RpcRequest;
import com.inno72.job.core.rpc.codec.RpcResponse;
import com.inno72.job.core.rpc.netcom.NetComServerFactory;
import com.inno72.job.core.rpc.serialize.HessianSerializer;
import com.inno72.job.core.util.HttpClientUtil;

@Controller
public class JobApiController {
    private static Logger logger = LoggerFactory.getLogger(JobApiController.class);

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

}
