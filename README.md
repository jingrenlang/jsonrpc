#JSON-RPC

目前只支持utf-8编码

##GET请求协议
* method：必须
* params:
    * 1、普通参数：name=value -> ["value"]
	* 2、对象参数：obj.a=v -> [{"a":"v"}]
	* 3、复合参数：name=value & obj.a=a & obj.b.c=c -> ["value",{"a":"a", "b":{"c":"c"}}]
	
##POST请求协议

接收JSON数据流

* id:请求编号
* method:方法名
* params:参数数组