#JSON-RPC

* 支持utf-8编码
* 方法不可重名
* 支持JSONP请求，参数名jsonpCallback、callback

##GET请求协议

* method：必须
* params:
    * 普通参数：name=value -> ["value"]
    * 对象参数：obj.a=v -> [{"a":"v"}]
    * 复合参数：name=value & obj.a=a & obj.b.c=c -> ["value",{"a":"a", "b":{"c":"c"}}]

请求的参数名与方法形式参数名一一匹配，`method=func&name=n&user.name=n`对应了方法`func(name,user)`

    GET请求：/com.jsonrpc.UserService?method=save&name=newname&user.name=oldname&user.age=18&user.ok=true
	转换后的JSON内容：{"method":"newname","params":["newname",{"name":"oldname","age":"1","ok":"true"}]}
	实际会调用com.jsonrpc.UserService.save(name,user)方法

##POST请求协议

接收JSON数据流

* id:请求编号
* method:方法名
* params:参数数组

##Spring集成

Server端

	<bean id="rpcServiceImpl" class="com.jsonrpc.RpcService" />

	<bean class="com.jsonrpc.server.spring.JsonrpcServerFactoryBean">
		<property name="port" value="8000" />
		<property name="poolSize" value="300" />
		<property name="services">
			<map>
				<entry key="com.jsonrpc.RpcService" value-ref="rpcServiceImpl" />
			</map>
		</property>
	</bean>

	//启动Server端
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		ctx.registerShutdownHook();
		ctx.close();
	}

Client端

	<bean id="JsonrpcProxyFactory" abstract="true" class="com.jsonrpc.client.spring.JsonrpcProxyFactoryBean">
		<property name="url" value="http://localhost:8000" />
		<property name="connectTimeout" value="2000" />
		<property name="readTimeout" value="3000" />
	</bean>

	<bean id="rpcService" parent="JsonrpcProxyFactory">
		<property name="interface" value="com.jsonrpc.RpcService" />
	</bean>
	
	//客户端访问服务
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		RpcService service = ctx.getBean(RpcService.class);
		System.out.println(service.exec());
		service.say("JSON RPC");
		System.out.println(service.returnNull());

		ctx.registerShutdownHook();
		ctx.close();
	}

