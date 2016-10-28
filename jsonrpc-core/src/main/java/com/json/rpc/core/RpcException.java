package com.json.rpc.core;

public class RpcException extends RuntimeException {

	private static final long serialVersionUID = -3195362650290569607L;

	public RpcException() {
		super();
	}

	public RpcException(Throwable cause) {
		super(cause);
	}

	public RpcException(String message) {
		super(message);
	}

}
