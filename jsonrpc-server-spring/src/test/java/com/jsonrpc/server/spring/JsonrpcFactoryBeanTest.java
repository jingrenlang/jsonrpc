package com.jsonrpc.server.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JsonrpcFactoryBeanTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		ctx.registerShutdownHook();
		ctx.close();
	}
}
