package com.jsonrpc.server.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JsonrpcFactoryBeanTest {

	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		Thread.sleep(Integer.MAX_VALUE);
		ctx.close();
	}
}
