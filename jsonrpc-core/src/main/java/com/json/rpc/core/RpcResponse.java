package com.json.rpc.core;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RPC响应
 * 
 * @author jingrenlang
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class RpcResponse implements Serializable {

	private static final long serialVersionUID = 3409245535132982803L;

	private String jsonrpc = "2.0";
	private long id;
	private Object result;
	private RpcError error;

	public static RpcResponse error(long id, int code, String message) {
		RpcResponse result = new RpcResponse();
		result.id = id;
		result.error = new RpcError(code, message);
		return result;
	}

	public static RpcResponse result(long id, Object value) {
		RpcResponse result = new RpcResponse();
		result.id = id;
		result.result = value;
		return result;
	}
}
