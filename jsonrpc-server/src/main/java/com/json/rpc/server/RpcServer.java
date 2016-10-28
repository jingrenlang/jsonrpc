package com.json.rpc.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * JSON RPC Server，接收RPC请求
 * 
 * @author jingrenlang
 *
 */
public class RpcServer {
	private static final String CONTEXT = "/";

	private int port = 8080;
	private int poolSize = 200;
	private RpcDispatcher dispatcher;

	public RpcServer(RpcDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public RpcDispatcher getRpcDispatcher() {
		return dispatcher;
	}

	public void setRpcDispatcher(RpcDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	private Server createServer(int port, int poolSize) {
		// 设置线程池大小
		QueuedThreadPool pool = new QueuedThreadPool(poolSize);
		pool.setName("JSON-RPC-SERVER");
		Server server = new Server(pool);
		// 设置在JVM退出时关闭Jetty的钩子。
		server.setStopAtShutdown(true);

		// 这是http的连接器
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		// 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[] { connector });
		return server;
	}

	private ServletContextHandler createServletHandler() {
		ServletContextHandler result = new ServletContextHandler();
		result.setContextPath(CONTEXT);

		result.addServlet(new ServletHolder(dispatcher), "/*");
		return result;
	}

	public void start() {
		final Server server = createServer(getPort(), getPoolSize());
		server.setHandler(createServletHandler());

		try {
			server.stop();
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
