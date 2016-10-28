package com.json.rpc.client;

import com.json.rpc.RpcService;

public class ProxyClientTest {

	public static void main(String[] args) {
		RpcConfig config = new RpcConfig();
		config.setUrl("http://localhost:8080");

		RpcService service = RpcClient.getService(config, RpcService.class);
		System.out.println(service.exec());
		service.say("JSON RPC");
		System.out.println(service.returnNull());
	}
}
