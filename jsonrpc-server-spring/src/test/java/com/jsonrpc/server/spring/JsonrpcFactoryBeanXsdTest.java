package com.jsonrpc.server.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JsonrpcFactoryBeanXsdTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-xsd.xml");
		ctx.registerShutdownHook();
		ctx.close();
	}
}
