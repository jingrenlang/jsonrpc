package com.json.rpc.server.utils;

import javassist.ClassPool;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

public final class ClassUtils {

	private static ClassPool pool = ClassPool.getDefault();

	/**
	 * 获取方法的形式参数名
	 */
	public static String[] getMethodArgsName(Class<?> clazz, String methodName) throws NotFoundException {
		CtMethod ctMethod = pool.get(clazz.getName()).getDeclaredMethod(methodName);
		CodeAttribute attr = ctMethod.getMethodInfo().getCodeAttribute();
		LocalVariableAttribute var = (LocalVariableAttribute) attr.getAttribute(LocalVariableAttribute.tag);
		int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
		String[] result = new String[ctMethod.getParameterTypes().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = var.variableName(i + pos);
		}
		return result;
	}
}
