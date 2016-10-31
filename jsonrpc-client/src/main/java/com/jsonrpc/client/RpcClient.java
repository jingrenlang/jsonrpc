package com.jsonrpc.client;

import java.lang.reflect.Proxy;

public class RpcClient {

	@SuppressWarnings("unchecked")
	public static <T> T getService(RpcConfig config, Class<T> service) {
		RpcInvoker invoker = new RpcInvoker(config);
		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[] { service }, invoker);
	}

}
