package com.jsonrpc.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jsonrpc.core.RpcRequest;
import com.jsonrpc.core.RpcResponse;
import com.jsonrpc.core.utils.IoUtils;
import com.jsonrpc.server.utils.HttpUtils;
import com.jsonrpc.server.utils.MethodInfo;

/**
 * RPC请求分发器
 * 
 * @author jingrenlang
 *
 */
public class RpcDispatcher extends HttpServlet {

	private static final long serialVersionUID = 2215164708635350018L;

	private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	private static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript;charset=utf-8";
	private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";

	private static final String SERVICE_LIST_PATH = "/service/list";
	private static final String ERROR_SERVICE_MISSING = "service is missing";
	private static final String ERROR_METHOD_MISSING = "method is missing";

	private Logger logger = LoggerFactory.getLogger(getClass());
	private RpcRegistry rpcRegistry;

	public RpcDispatcher(RpcRegistry rpcRegistry) {
		super();
		this.rpcRegistry = rpcRegistry;
	}

	public void setRpcRegistry(RpcRegistry rpcRegistry) {
		this.rpcRegistry = rpcRegistry;
	}

	public RpcRegistry getRpcRegistry() {
		return rpcRegistry;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/** GET请求主要是兼容页面的AJAX、JSONP */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String context = request.getPathInfo();
		if (SERVICE_LIST_PATH.equals(context)) {
			outputRpcServiceList(response);
			return;
		}
		if (context == null || context.trim().length() == 0) {
			ouputJSON(response, RpcResponse.error(0, 404, ERROR_SERVICE_MISSING));
			return;
		}
		String serviceName = context.substring(1);
		if (!rpcRegistry.contains(serviceName)) {
			ouputJSON(response, RpcResponse.error(0, 404, ERROR_SERVICE_MISSING));
			return;
		}
		String methodName = request.getParameter("method");
		MethodInfo methodInfo = rpcRegistry.find(serviceName).findMethodInfo(methodName);
		if (methodInfo == null) {
			ouputJSON(response, RpcResponse.error(0, 404, ERROR_METHOD_MISSING));
			return;
		}
		logger.debug("RPC {} GET Request parameters: {}", serviceName, request.getParameterMap());

		// 解析请求参数
		Object[] args = parseArgs(request, methodInfo);
		// 构建请求内容JSON
		String jsonText = buildRequestJson(methodName, args);
		logger.debug("RPC {} GET Request jsonText: {}", serviceName, jsonText);

		// 业务处理
		RpcRequest rpcRequest = JSON.parseObject(jsonText, RpcRequest.class);
		RpcResponse rpcResponse = rpcRegistry.find(serviceName).execute(rpcRequest);

		// 对JSONP的支持
		String jsonpCallback = HttpUtils.getJsonpParameter(request);
		if (jsonpCallback != null) {
			response.setContentType(CONTENT_TYPE_JAVASCRIPT);
			String jsonResult = JSON.toJSONString(rpcResponse);
			StringBuilder jsonp = new StringBuilder(jsonpCallback).append("(").append(jsonResult).append(")");
			response.getWriter().write(jsonp.toString());
			return;
		}
		// 响应业务结果
		response.setContentType(CONTENT_TYPE_JSON);
		ouputJSON(response, rpcResponse);
	}

	/** 构建请求内容JSON */
	private String buildRequestJson(String method, Object[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", 100000000);
		map.put("method", method);
		map.put("params", args);
		return JSON.toJSONString(map);
	}

	// GET请求方式支持， 默认规范：（参数类型如何转换？反序列化时，fastjson会自动处理）
	// _____1、普通参数：name=value -> ["value"]
	// _____2、对象属性：obj.a=va -> [{"a":"va"}]
	// _____3、复合参数：name=value&obj.a=a&obj.b.c=c -> ["value",{"a":"a", "b":{"c":"c"}}]
	@SuppressWarnings("unchecked")
	private Object[] parseArgs(HttpServletRequest request, MethodInfo methodInfo) {
		Map<String, Object> paramsMap = new HashMap<>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if (!name.contains(".")) {
				paramsMap.put(name, HttpUtils.parameterValue(request, name));
				continue;
			}

			//分析嵌套参数
			String[] nestProperties = name.split("\\.");
			String topName = nestProperties[0];
			if (!paramsMap.containsKey(topName)) {
				paramsMap.put(topName, new HashMap<>());
			}

			Map<String, Object> nestMap = (Map<String, Object>) paramsMap.get(topName);
			for (int i = 1; i < nestProperties.length; i++) {
				String key = nestProperties[i];
				if (i == nestProperties.length - 1) {
					nestMap.put(key, HttpUtils.parameterValue(request, name));
					continue;
				}
				if (!nestMap.containsKey(key)) {
					nestMap.put(key, new HashMap<String, Object>());
				}
				nestMap = (Map<String, Object>) nestMap.get(key);
			}
		}

		Object[] args = new Object[methodInfo.getArgsName().length];
		for (int i = 0; i < args.length; i++) {
			args[i] = paramsMap.get(methodInfo.getArgsName()[i]);
		}
		return args;
	}

	/** 输出当前注册的RPC服务列表 */
	private void outputRpcServiceList(HttpServletResponse response) throws IOException {
		String head = "<head><title>JSON RPC Service List</title><style type='text/css'>table{border-collapse:collapse;border:none;width:800px;margin:0 auto;}td{border:solid #000 1px;}</style></head>";
		StringBuilder html = new StringBuilder("<html>").append(head).append("<body><h2 style='text-align:center;'>JSON RPC Service List</h2><table>");
		rpcRegistry.getHandlers().forEach(handler -> {
			html.append("<tr><td style='width:300px;'>").append(handler.getServiceClass().getName()).append("</td><td style='padding-left: 10px;'>");
			handler.getMethodInfos().forEach(m -> html.append("<div>").append(m.toString()).append("</div>"));
			html.append("</td></tr>");
		});
		html.append("</table></body></html>");

		response.setContentType(CONTENT_TYPE_HTML);
		response.getWriter().write(html.toString());
		response.getWriter().flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String context = request.getPathInfo();
		if (context == null || context.trim().length() == 0) {
			ouputJSON(response, RpcResponse.error(0, 404, ERROR_SERVICE_MISSING));
			return;
		}
		String serviceName = context.substring(1);
		if (!rpcRegistry.contains(serviceName)) {
			ouputJSON(response, RpcResponse.error(0, 404, ERROR_SERVICE_MISSING));
			return;
		}

		// 1、读取数据流
		byte[] data = IoUtils.readStream(request.getInputStream(), request.getContentLength());
		// 2、转成JSON字符串
		String jsonText = new String(data);
		logger.debug("RPC {} POST Request jsonText: {}", serviceName, jsonText);

		RpcRequest rpcRequest = JSON.parseObject(jsonText, RpcRequest.class);
		// 3、业务处理
		RpcResponse rpcResponse = rpcRegistry.find(serviceName).execute(rpcRequest);
		// 4、响应处理结果
		ouputJSON(response, rpcResponse);
	}

	private void ouputJSON(HttpServletResponse response, RpcResponse rpcResponse) throws IOException {
		response.setContentType(CONTENT_TYPE_JSON);
		response.getWriter().println(JSON.toJSONString(rpcResponse));
	}

}
