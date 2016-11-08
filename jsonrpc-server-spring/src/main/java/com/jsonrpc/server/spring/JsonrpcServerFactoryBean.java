package com.jsonrpc.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.jsonrpc.server.RpcDispatcher;
import com.jsonrpc.server.RpcRegistry;
import com.jsonrpc.server.RpcServer;

public class JsonrpcServerFactoryBean implements FactoryBean<RpcServer>, InitializingBean {

	private int port = 8080;
	private int poolSize = 200;
	private Map<Class<?>, Object> services = new HashMap<>();

	private RpcServer server;

	public void setPort(int port) {
		this.port = port;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setServices(Map<Class<?>, Object> services) {
		this.services = services;
	}

	@Override
	public RpcServer getObject() throws Exception {
		return server;
	}

	@Override
	public Class<?> getObjectType() {
		return RpcServer.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (server == null) {
			RpcRegistry registry = new RpcRegistry();
			for (Class<?> _interface : services.keySet()) {
				registry.register(_interface, services.get(_interface));
			}

			server = new RpcServer(new RpcDispatcher(registry));
			server.setPort(port);
			server.setPoolSize(poolSize);
			server.start();
		}
	}

}
