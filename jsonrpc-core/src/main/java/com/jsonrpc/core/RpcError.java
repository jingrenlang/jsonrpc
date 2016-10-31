package com.jsonrpc.core;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC调用错误
 * 
 * @author jingrenlang
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RpcError implements Serializable {

	private static final long serialVersionUID = -6416338210041701415L;

	private int code;
	private String message;

}
