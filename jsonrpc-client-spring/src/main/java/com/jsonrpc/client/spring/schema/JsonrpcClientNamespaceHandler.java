package com.jsonrpc.client.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JsonrpcClientNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new JsonrpcConfigBeanDefinitionParser());
		registerBeanDefinitionParser("reference", new JsonrpcReferenceBeanDefinitionParser());
	}

}
