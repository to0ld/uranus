namespace java net.popbean.pf.service.a

service EchoService {
	string execute(1:string name) 
}

namespace java net.popbean.pf.service.b

service FooService {
	string bar(1:string name) 
}
