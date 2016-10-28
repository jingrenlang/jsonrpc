package com.json.rpc.server;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.json.rpc.core.RpcRequest;
import com.json.rpc.core.RpcResponse;

/**
 * RPC业务处理器
 * 
 * @author jingrenlang
 *
 */
public class RpcHandler {

	private Class<?> serviceClass;
	private Object serviceImpl;
	private Map<String, Method> methodMap = new HashMap<>();

	public RpcHandler(Class<?> serviceClass, Object serviceImpl) {
		super();
		this.serviceClass = serviceClass;
		this.serviceImpl = serviceImpl;

		Method[] methods = serviceClass.getDeclaredMethods();
		for (Method method : methods) {
			methodMap.put(method.getName(), method);
		}
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}

	Collection<Method> getMethods() {
		return methodMap.values();
	}

	public RpcResponse execute(RpcRequest request) {
		// 1、检测服务方法
		Method method = this.methodMap.get(request.getMethod());
		if (method == null) {
			return RpcResponse.error(request.getId(), 404, "method is missing");
		}

		Class<?>[] paramTypes = method.getParameterTypes();
		Object[] paramValues = request.getParams();
		if (paramTypes.length != paramValues.length) {
			return RpcResponse.error(request.getId(), 400, "method and params is mismatch");
		}

		// 2、反序列化参数值
		Object[] methodArgs = new Object[paramTypes.length];
		for (int i = 0; i < methodArgs.length; i++) {
			Class<?> type = paramTypes[i];
			String value = paramValues[i].toString();
			if (String.class == type) {
				methodArgs[i] = value;
				continue;
			}
			methodArgs[i] = JSON.parseObject(value, type);
		}

		// 3、调用业务方法
		try {
			Object result = method.invoke(serviceImpl, methodArgs);
			return RpcResponse.result(request.getId(), result);
		} catch (Exception e) {
			e.printStackTrace();
			return RpcResponse.error(request.getId(), 500, "service occur exception");
		}

	}

}
