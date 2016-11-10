package com.jsonrpc.server;

import com.jsonrpc.RpcService;
import com.jsonrpc.UserService;
import com.jsonrpc.server.RpcDispatcher;
import com.jsonrpc.server.RpcRegistry;
import com.jsonrpc.server.RpcServer;

public class RpcServerTest {

	public static void main(String[] args) throws Exception {
		RpcRegistry registry = new RpcRegistry();
		registry.register(RpcService.class, new RpcService());
		registry.register(UserService.class, new UserService());

		RpcDispatcher dispatcher = new RpcDispatcher(registry);
		RpcServer server = new RpcServer(dispatcher);
		server.start();
		Thread.sleep(Integer.MAX_VALUE);
	}
}
