package com.jsonrpc.client.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.jsonrpc.client.RpcClient;
import com.jsonrpc.client.RpcConfig;

public class JsonrpcProxyFactoryBean<T> implements FactoryBean<T>, InitializingBean {

	private Class<T> interfaceClass;
	private String url;
	private int connectTimeout = 2000;
	private int readTimeout = 3000;

	private T proxy;

	public void setInterface(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (proxy == null) {
			RpcConfig config = new RpcConfig();
			config.setUrl(url);
			config.setConnectTimeout(connectTimeout);
			config.setReadTimeout(readTimeout);
			proxy = RpcClient.getService(config, interfaceClass);
		}
	}

	@Override
	public T getObject() throws Exception {
		return proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
