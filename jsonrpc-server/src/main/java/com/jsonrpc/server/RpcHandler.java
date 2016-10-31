package com.jsonrpc.server;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jsonrpc.core.RpcRequest;
import com.jsonrpc.core.RpcResponse;
import com.jsonrpc.server.utils.ClassUtils;
import com.jsonrpc.server.utils.MethodInfo;

import javassist.NotFoundException;

/**
 * RPC业务处理器
 * 
 * @author jingrenlang
 *
 */
public class RpcHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private Class<?> serviceClass;
	private Object serviceImpl;
	private Map<String, MethodInfo> methodMap = new HashMap<>();

	public RpcHandler(Class<?> serviceClass, Object serviceImpl) {
		super();
		this.serviceClass = serviceClass;
		this.serviceImpl = serviceImpl;

		Method[] methods = serviceClass.getDeclaredMethods();
		for (Method method : methods) {
			try {
				String[] argsName = ClassUtils.getMethodArgsName(serviceClass, method.getName());
				MethodInfo info = new MethodInfo(method, argsName);
				methodMap.put(method.getName(), info);

				logger.debug("Add RPC Handler {}@{}", serviceClass.getName(), info.toString());
			} catch (NotFoundException e) {
				logger.error("obtain " + serviceClass.getName() + "@" + method.getName() + " args name occur exception", e);
			}
		}
	}

	Class<?> getServiceClass() {
		return serviceClass;
	}

	Collection<MethodInfo> getMethodInfos() {
		return methodMap.values();
	}

	MethodInfo findMethodInfo(String methodName) {
		return methodMap.get(methodName);
	}

	public RpcResponse execute(RpcRequest request) {
		// 1、检测服务方法
		MethodInfo methodInfo = this.methodMap.get(request.getMethod());
		if (methodInfo == null) {
			return RpcResponse.error(request.getId(), 404, "method is missing");
		}

		Class<?>[] paramTypes = methodInfo.getMethod().getParameterTypes();
		Object[] paramValues = request.getParams();
		if (paramTypes.length != paramValues.length) {
			return RpcResponse.error(request.getId(), 400, "method and params is mismatch");
		}

		// 2、反序列化参数值
		Object[] methodArgs = new Object[paramTypes.length];
		try {
			for (int i = 0; i < methodArgs.length; i++) {
				if (paramValues[i] == null) {
					methodArgs[i] = null;
					continue;
				}

				Class<?> type = paramTypes[i];
				String value = paramValues[i].toString();
				if (String.class == type) {
					methodArgs[i] = value;
					continue;
				}
				methodArgs[i] = JSON.parseObject(value, type);
			}
		} catch (Exception e) {
			logger.error("deserialize " + serviceClass.getName() + "@" + methodInfo + " args occur exception", e);
			return RpcResponse.error(request.getId(), 501, "params deserialize occur exception");
		}

		// 3、调用业务方法
		try {
			Object result = methodInfo.getMethod().invoke(serviceImpl, methodArgs);
			return RpcResponse.result(request.getId(), result);
		} catch (Exception e) {
			logger.error("invoke " + serviceClass.getName() + "@" + methodInfo + " occur exception", e);
			return RpcResponse.error(request.getId(), 500, "service occur exception");
		}

	}

}
