package net.popbean.pf.soa.server.helper.gen.domain;

import java.lang.reflect.Type;


public class ThriftMethodArg {
	public Generic genericType;
	
	public String name;
	//
	public ThriftMethodArg(){
		super();
	}
	public ThriftMethodArg(Type type, String paramName) {//先凑活用吧
		super();
		this.name = paramName;
	}
}
