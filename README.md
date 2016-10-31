#JSON-RPC

目前只支持utf-8编码，方法不可重名

##GET请求协议

* method：必须
* params:
    * 1、普通参数：name=value -> ["value"]
    * 2、对象参数：obj.a=v -> [{"a":"v"}]
    * 3、复合参数：name=value & obj.a=a & obj.b.c=c -> ["value",{"a":"a", "b":{"c":"c"}}]

请求的参数名与方法形式参数名一一匹配，`method=func&name=n&user.name=n`对应了方法`func(name,user)`

    GET请求：/com.jsonrpc.UserService?method=save&name=newname&user.name=oldname&user.age=18&user.ok=true
	转换后的JSON内容：{"method":"newname","params":["newname",{"name":"oldname","age":"1","ok":"true"}]}
	实际会调用com.jsonrpc.UserService.save(name,user)方法

##POST请求协议

接收JSON数据流

* id:请求编号
* method:方法名
* params:参数数组