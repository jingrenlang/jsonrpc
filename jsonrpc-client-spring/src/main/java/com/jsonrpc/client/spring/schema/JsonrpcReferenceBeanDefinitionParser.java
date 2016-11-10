package com.jsonrpc.client.spring.schema;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.jsonrpc.client.spring.JsonrpcProxyFactoryBean;

public class JsonrpcReferenceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return JsonrpcProxyFactoryBean.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String interfaceClass = element.getAttribute("interface");
		String url = element.getAttribute("url");
		String connectTimeout = element.getAttribute("connect-timeout");
		String readTimeout = element.getAttribute("read-timeout");

		bean.addPropertyValue("interface", interfaceClass);
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("url", url);
		}
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("connectTimeout", connectTimeout);
		}
		if (StringUtils.hasText(url)) {
			bean.addPropertyValue("readTimeout", readTimeout);
		}

		String config = element.getAttribute("config");
		if (StringUtils.hasText(config)) {
			bean.addPropertyValue("config", new RuntimeBeanReference(config));
		}
	}

}
