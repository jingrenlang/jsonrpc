package com.jsonrpc;

import java.math.BigDecimal;
import java.util.Map;

public interface RpcService {

	public Map<String, Object> exec();

	public String say(String name);

	public boolean bool();

	public int tint();

	public float tfloat();

	public BigDecimal decimal();

	public Object returnNull();

}
