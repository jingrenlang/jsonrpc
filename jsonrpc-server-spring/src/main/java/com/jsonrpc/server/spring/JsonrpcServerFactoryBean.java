package com.jsonrpc.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jsonrpc.server.RpcDispatcher;
import com.jsonrpc.server.RpcRegistry;
import com.jsonrpc.server.RpcServer;

public class JsonrpcServerFactoryBean implements FactoryBean<RpcServer>, InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private int port = 8080;
	private int poolSize = 200;
	private Map<Class<?>, Object> services = new HashMap<>();

	private RpcServer server;
	private ApplicationContext ctx;

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
				Object impl = services.get(_interface);
				if (impl instanceof RuntimeBeanReference) {//很丑陋
					impl = ctx.getBean(((RuntimeBeanReference) impl).getBeanName());
				}
				registry.register(_interface, impl);
			}

			server = new RpcServer(new RpcDispatcher(registry));
			server.setPort(port);
			server.setPoolSize(poolSize);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {//等待根容器启动完毕
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		server.stop();
	}

}
