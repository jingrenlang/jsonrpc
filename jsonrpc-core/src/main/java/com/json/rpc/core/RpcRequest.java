package com.json.rpc.core;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RPC请求
 * 
 * @author jingrenlang
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class RpcRequest implements Serializable {

	private static final long serialVersionUID = 4302955022437111979L;

	private long id;
	private String method;
	private Object[] params;
}
