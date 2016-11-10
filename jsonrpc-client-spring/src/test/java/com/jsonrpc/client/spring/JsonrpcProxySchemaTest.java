package com.jsonrpc.client.spring;

import java.util.Arrays;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jsonrpc.RpcService;
import com.jsonrpc.UserService;

public class JsonrpcProxySchemaTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-schema.xml");
		RpcService service = ctx.getBean(RpcService.class);
		System.out.println(service.exec());
		System.out.println(service.say("JSON RPC"));
		System.out.println(service.returnNull());
		System.out.println(service.bool());
		System.out.println(service.tint());
		System.out.println(service.tfloat());
		System.out.println(service.decimal());

		UserService userService = ctx.getBean(UserService.class);
		userService.findUsers("").forEach(u -> System.out.print(u));
		System.out.println();
		System.out.println(Arrays.toString(userService.array()));

		ctx.registerShutdownHook();
		ctx.close();
	}
}
