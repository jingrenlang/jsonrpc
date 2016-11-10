package com.jsonrpc;

import java.math.BigDecimal;
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

	public String say(String name) {
		return "Hello," + name;
	}

	public Object returnNull() {
		return null;
	}

	public String method(String s, int i, double d) {
		return null;
	}

	public boolean bool() {
		return true;
	}

	public int tint() {
		return 1;
	}

	public float tfloat() {
		return 1.23f;
	}

	public BigDecimal decimal() {
		return new BigDecimal("1.23333");
	}

}
