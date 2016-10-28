package com.json.rpc.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.json.rpc.core.RpcRequest;
import com.json.rpc.core.RpcResponse;
import com.json.rpc.core.utils.IoUtils;

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

		// 解析请求参数
		String method = request.getParameter("method");
		Object[] args = parseArgs(request);
		// 构建请求内容JSON
		String jsonText = buildRequestJson(method, args);
		// 业务处理
		RpcRequest rpcRequest = JSON.parseObject(jsonText, RpcRequest.class);
		RpcResponse rpcResponse = rpcRegistry.find(serviceName).execute(rpcRequest);

		// 对JSONP的支持
		String jsonpCallback = findJsonpCallback(request);
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
	// _____1、普通参数：args[0]=value -> ["value"]
	// _____2、对象属性：args[0].a=va -> [{"a":"va"}]
	// _____3、复合参数：args[0]=value&args[1].a=a&args[1].b.c=c -> ["value",{"a":"a", "b":{"c":"c"}}]
	@SuppressWarnings("unchecked")
	private Object[] parseArgs(HttpServletRequest request) {
		int maxIndex = 0;
		Map<Integer, Object> argsMap = new HashMap<>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if (!name.startsWith("args")) {
				continue;
			}

			int start = name.indexOf("[");
			int end = name.indexOf("]");
			int argIndex = Integer.valueOf(name.substring(start + 1, end));
			maxIndex = Math.max(argIndex, maxIndex);

			if (!name.contains(".")) {
				argsMap.put(argIndex, parameterValue(request, name));
				continue;
			}

			//分析嵌套参数
			Object arg = argsMap.get(argIndex);
			if (arg == null) {
				arg = new HashMap<String, Object>();
				argsMap.put(argIndex, arg);
			}
			Map<String, Object> map = (Map<String, Object>) arg;
			String[] nestProperties = name.split("\\.");
			for (int i = 1; i < nestProperties.length; i++) {
				String key = nestProperties[i];
				if (i == nestProperties.length - 1) {
					map.put(key, parameterValue(request, name));
					continue;
				}

				if (!map.containsKey(key)) {
					map.put(key, new HashMap<String, Object>());
				}
				map = (Map<String, Object>) map.get(key);
			}
		}

		Object[] args = new Object[maxIndex + 1];
		for (int i = 0; i < args.length; i++) {
			args[i] = argsMap.get(i);
		}
		return args;
	}

	private Object parameterValue(HttpServletRequest request, String name) {
		String[] value = request.getParameterValues(name);
		if (value == null) {
			return null;
		}
		if (value.length == 1) {
			return value[0];
		}
		return value;
	}

	private String findJsonpCallback(HttpServletRequest request) {
		String callback = request.getParameter("jsonpCallback");
		if (callback == null) {
			callback = request.getParameter("callback");
		}
		return callback;
	}

	/** 输出当前注册的RPC服务列表 */
	private void outputRpcServiceList(HttpServletResponse response) throws IOException {
		String head = "<head>"//
				+ "<title>JSON RPC Service List</title>"//
				+ "<style type='text/css'>table{border-collapse:collapse;border:none;width:800px;margin:0 auto;}td{border:solid #000 1px;}</style>"//
				+ "</head>";
		StringBuilder html = new StringBuilder("<html>").append(head).append("<body><h2 style='text-align:center;'>JSON RPC Service List</h2><table>");
		rpcRegistry.getHandlers().forEach(handler -> {
			html.append("<tr>")//
					.append("<td style='width:300px;'>").append(handler.getServiceClass().getName()).append("</td>")// 服务类名称
					.append("<td style='padding-left: 10px;'>");
			handler.getMethods().forEach(m -> {
				Class<?>[] paramTypes = m.getParameterTypes();
				String[] params = new String[paramTypes.length];
				for (int i = 0; i < params.length; i++) {
					params[i] = paramTypes[i].getSimpleName();
				}
				html.append("<div>")//
						.append(m.getReturnType().getSimpleName()).append(" ")// 返回值类型
						.append(m.getName())// 方法名
						.append("(").append(String.join(",", params)).append(")")// 参数列表类型
						.append("</div>");
			});
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
