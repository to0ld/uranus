package net.popbean.pf.soa.server.helper.gen.domain;

import java.util.ArrayList;
import java.util.List;

public class ThriftMethod {
	public Generic returnGenericType;
	
	public String name;
	
	public List<ThriftMethodArg> methodArgs = new ArrayList<ThriftMethodArg>();
	
	public List<Class<?>> relationClasses;
}
