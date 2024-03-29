package com.inno72.job.core.rpc.netcom.jetty.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inno72.job.core.rpc.codec.RpcRequest;
import com.inno72.job.core.rpc.codec.RpcResponse;
import com.inno72.job.core.rpc.netcom.NetComServerFactory;
import com.inno72.job.core.rpc.serialize.HessianSerializer;
import com.inno72.job.core.util.HttpClientUtil;


public class JettyServerHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(JettyServerHandler.class);

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// invoke
        RpcResponse rpcResponse = doInvoke(request);

        // serialize response
        byte[] responseBytes = HessianSerializer.serialize(rpcResponse);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();
		
	}

	private RpcResponse doInvoke(HttpServletRequest request) {
		try {
			// deserialize request
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

}
