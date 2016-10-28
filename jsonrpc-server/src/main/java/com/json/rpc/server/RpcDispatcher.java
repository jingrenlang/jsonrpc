package com.json.rpc.server;

import java.io.IOException;

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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().write(html.toString());
		resp.getWriter().flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=utf-8");

		String context = request.getPathInfo();
		if (context == null || context.trim().length() == 0) {
			ouput(response, RpcResponse.error(0, 404, "service is missing"));
			return;
		}

		String serviceName = context.substring(1);
		if (!rpcRegistry.contains(serviceName)) {
			ouput(response, RpcResponse.error(0, 404, "service is missing"));
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
		ouput(response, rpcResponse);
	}

	private void ouput(HttpServletResponse response, RpcResponse rpcResponse) throws IOException {
		response.getWriter().println(JSON.toJSONString(rpcResponse));
	}

}
