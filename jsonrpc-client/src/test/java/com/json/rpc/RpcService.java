package com.json.rpc;

import java.util.Map;

public interface RpcService {

	public Map<String, Object> exec();

	public void say(String name);

	public Object returnNull();

}
