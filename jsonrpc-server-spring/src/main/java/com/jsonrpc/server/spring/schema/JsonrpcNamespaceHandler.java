package com.jsonrpc.server.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JsonrpcNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("server", new JsonrpcBeanDefinitionParser());
	}

}
