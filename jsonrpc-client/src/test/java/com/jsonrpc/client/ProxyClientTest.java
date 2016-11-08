package com.jsonrpc.client;

import com.jsonrpc.RpcService;
import com.jsonrpc.client.RpcClient;
import com.jsonrpc.client.RpcConfig;

public class ProxyClientTest {

	public static void main(String[] args) {
		RpcConfig config = new RpcConfig();
		config.setUrl("http://localhost:8000");

		RpcService service = RpcClient.getService(config, RpcService.class);
		System.out.println(service.exec());
		service.say("JSON RPC");
		System.out.println(service.returnNull());
	}
}
