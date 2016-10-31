package com.jsonrpc.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC handler 注册表
 * 
 * @author jingrenlang
 *
 */
public class RpcRegistry {

	private Map<String, RpcHandler> registry = new HashMap<>();

	public void register(Class<?> _interface, Object impl) {
		registry.put(_interface.getName(), new RpcHandler(_interface, impl));
	}

	public boolean contains(String service) {
		return registry.containsKey(service);
	}

	public RpcHandler find(String service) {
		return registry.get(service);
	}

	Collection<RpcHandler> getHandlers() {
		return registry.values();
	}
}
