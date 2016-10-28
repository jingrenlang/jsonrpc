package com.json.rpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.json.rpc.core.RpcException;
import com.json.rpc.core.RpcRequest;
import com.json.rpc.core.RpcResponse;
import com.json.rpc.core.utils.IoUtils;

public class RpcInvoker implements InvocationHandler {

	private AtomicLong sequence = new AtomicLong();

	private RpcConfig config;

	public RpcInvoker(RpcConfig config) {
		super();
		this.config = config;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RpcRequest request = new RpcRequest();
		request.setId(sequence.incrementAndGet());
		request.setMethod(method.getName());
		request.setParams(args == null ? new Object[0] : args);

		byte[] readBytes = sendRequest(method.getDeclaringClass(), request);
		String jsonText = new String(readBytes);
		RpcResponse rpcResponse = JSON.parseObject(jsonText, RpcResponse.class);

		// RPC调用出错
		if (rpcResponse.getError() != null) {
			throw new RpcException(rpcResponse.getError().toString());
		}

		// 返回值本身为null
		Object result = rpcResponse.getResult();
		if (result == null) {
			return null;
		}

		Class<?> returnClass = method.getReturnType();
		if (String.class == returnClass) {
			return result.toString();
		}
		return JSON.parseObject(result.toString(), returnClass);
	}

	private byte[] sendRequest(Class<?> _interface, RpcRequest request) {
		String url = config.getUrl() + "/" + _interface.getName();
		byte[] jsonBytes = JSON.toJSONString(request).getBytes();

		// 构建HTTP链接
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(config.getConnectTimeout());
			connection.setReadTimeout(config.getReadTimeout());
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			connection.setRequestProperty("Content-Length", "" + jsonBytes.length);
			connection.connect();
		} catch (IOException e) {
			throw new RpcException(e);
		}

		// 输出数据
		try (OutputStream out = connection.getOutputStream()) {
			out.write(jsonBytes);
			out.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RpcException("Http request error,statuscode = " + connection.getResponseCode());
			}
		} catch (IOException e) {
			throw new RpcException(e);
		}

		// 读取响应数据
		try (InputStream in = connection.getInputStream()) {
			return IoUtils.readStream(in, connection.getContentLength());
		} catch (IOException e) {
			throw new RpcException(e);
		}
	}
}
