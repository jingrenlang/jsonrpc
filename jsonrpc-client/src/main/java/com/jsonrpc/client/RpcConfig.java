package com.jsonrpc.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RpcConfig {

	private String url;
	private int connectTimeout = 2000;
	private int readTimeout = 3000;

}
