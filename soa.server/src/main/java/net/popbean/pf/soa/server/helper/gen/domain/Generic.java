package net.popbean.pf.soa.server.helper.gen.domain;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



public class Generic extends ThriftType {
	public List<? super ThriftType> types = new ArrayList<ThriftType>();
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<");
		for (Object type : types) {
			if(type instanceof Generic) {
				sb.append(type.toString());
			}else {
				ThriftType thriftType = (ThriftType) type;
				if(thriftType.isStruct()) {
					sb.append(thriftType.value);
				}else {
					sb.append(thriftType.warpperClassName);
				}
			}
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String toThriftString() {
		if(types == null || types.isEmpty()) {
			return this.value;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(this.value);
		sb.append("<");
		for (int i = 0; i < types.size(); i++) {
			Object type = types.get(i);
			ThriftType thriftType = (ThriftType) type;
			
			if(type instanceof Generic) {
				sb.append(((Generic) type).toThriftString());
			}else {
				sb.append(thriftType.value);
			}
			
			if(i != types.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(">");
		return sb.toString();
	}
	
	public static Generic fromType(Type type) {
		Generic generic = new Generic();
		if(!(type instanceof ParameterizedType)) {
			ThriftType thriftType = ThriftType.fromJavaType(type);
			generic.javaClass = thriftType.javaClass;
			generic.javaTypeName = thriftType.javaTypeName;
			generic.value = thriftType.value;
			generic.warpperClassName = thriftType.warpperClassName;
			generic.type = thriftType.type;
			return generic;
		}
		ThriftType thriftType = ThriftType.fromJavaType(type);
		generic.value = thriftType.value;
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Type[] types = parameterizedType.getActualTypeArguments();
		for (Type typeArgument : types) {
			if(typeArgument instanceof ParameterizedType) {
				generic.types.add(fromType(typeArgument));
				continue;
			}
			ThriftType typeArgumentThriftType = ThriftType.fromJavaType((Class<?>)typeArgument);
			if(typeArgumentThriftType.isStruct()) {
				typeArgumentThriftType = typeArgumentThriftType.clone();
				typeArgumentThriftType.javaClass = (Class<?>)typeArgument;
				typeArgumentThriftType.value = ((Class<?>)typeArgument).getSimpleName();
			}
			generic.types.add(typeArgumentThriftType);
		}
		return generic;
	}
}
