package com.jsonrpc.server.spring.schema;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jsonrpc.server.spring.JsonrpcServerFactoryBean;

public class JsonrpcBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return JsonrpcServerFactoryBean.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String portValue = element.getAttribute("port");
		String poolSizeValue = element.getAttribute("pool-size");
		if (StringUtils.hasText(portValue)) {
			bean.addPropertyValue("port", portValue);
		}
		if (StringUtils.hasText(poolSizeValue)) {
			bean.addPropertyValue("poolSize", poolSizeValue);
		}

		Map<String, Object> interfaces = new HashMap<>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			Element ele = (Element) node;
			if (!"service".equals(ele.getLocalName())) {
				continue;
			}

			String interfaceClass = ele.getAttribute("interface");
			String ref = ele.getAttribute("ref");
			//此处如何获取真实的spring bean?
			interfaces.put(interfaceClass, new RuntimeBeanReference(ref));
		}

		bean.addPropertyValue("services", interfaces);
		bean.setLazyInit(false);
	}

}
