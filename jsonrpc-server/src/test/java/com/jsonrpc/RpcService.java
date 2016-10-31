package com.jsonrpc;

import java.util.HashMap;
import java.util.Map;

public class RpcService {

	public Map<String, Object> exec() {
		Map<String, Object> map = new HashMap<>();
		map.put("a", "1");
		map.put("b", 2);
		map.put("c", true);
		return map;
	}

	public void say(String name) {
		System.out.println("Hello," + name);
	}

	public Object returnNull() {
		return null;
	}

	public String method(String s, int i, double d) {
		return null;
	}
}
