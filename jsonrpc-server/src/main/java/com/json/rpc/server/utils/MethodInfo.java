package com.json.rpc.server.utils;

import java.lang.reflect.Method;

public class MethodInfo {
	private Method method;
	private String[] argsName;

	public MethodInfo(Method m, String[] argsName) {
		super();
		this.method = m;
		this.argsName = argsName;
	}

	public Method getMethod() {
		return method;
	}

	public String[] getArgsName() {
		return argsName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(method.getReturnType().getSimpleName()).append(" ")//
				.append(method.getName()).append("(");
		Class<?>[] argsType = method.getParameterTypes();
		for (int i = 0; i < argsType.length; i++) {
			builder.append(argsType[i].getSimpleName()).append(" ").append(argsName[i]);
			if (i < argsType.length - 1) {
				builder.append(", ");
			}
		}
		return builder.append(")").toString();
	}

}
