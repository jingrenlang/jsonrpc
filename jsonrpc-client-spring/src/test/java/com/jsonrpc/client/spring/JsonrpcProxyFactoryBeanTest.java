package com.jsonrpc.client.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jsonrpc.RpcService;

public class JsonrpcProxyFactoryBeanTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		RpcService service = ctx.getBean(RpcService.class);
		System.out.println(service.exec());
		service.say("JSON RPC");
		System.out.println(service.returnNull());

		ctx.registerShutdownHook();
		ctx.close();
	}
}
