package com.json.rpc.server;

import com.json.rpc.RpcService;
import com.json.rpc.UserService;

public class RpcServerTest {

	public static void main(String[] args) {
		RpcRegistry registry = new RpcRegistry();
		registry.register(RpcService.class, new RpcService());
		registry.register(UserService.class, new UserService());

		RpcDispatcher dispatcher = new RpcDispatcher(registry);
		RpcServer server = new RpcServer(dispatcher);
		server.start();
	}
}
