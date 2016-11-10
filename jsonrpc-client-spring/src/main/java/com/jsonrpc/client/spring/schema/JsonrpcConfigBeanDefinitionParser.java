package com.jsonrpc.client.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jsonrpc.client.RpcConfig;

public class JsonrpcConfigBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return RpcConfig.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String url = element.getAttribute("url");
		String connectTimeout = element.getAttribute("connect-timeout");
		String readTimeout = element.getAttribute("read-timeout");

		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("url", url);
		}
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("connectTimeout", connectTimeout);
		}
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("readTimeout", readTimeout);
		}
	}

}
