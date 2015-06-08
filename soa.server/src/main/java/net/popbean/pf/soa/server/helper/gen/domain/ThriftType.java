package net.popbean.pf.soa.server.helper.gen.domain;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;




/**
 * 
 * @author to0ld
 *
 */
public class ThriftType implements Cloneable {
	public String value;
	public int type;
	public String javaTypeName;
	public Class<?> javaClass;
	public String warpperClassName;
	//
	public static final int BASIC_TYPE = 1;
	public static final int COLLECTION_TYPE = 1 << 1;
	public static final int STRUCT_TYPE = 1 << 2;
	public static final int VOID_TYPE = 1 << 3;
	public static final int ENUM_TYPE = 1 << 4;
	//
	public static final ThriftType BOOL = new ThriftType("bool", BASIC_TYPE, "boolean", "Boolean");
	public static final ThriftType BYTE = new ThriftType("byte", BASIC_TYPE, "byte", "Byte");
	public static final ThriftType I16 = new ThriftType("i16", BASIC_TYPE, "short", "Short");
	public static final ThriftType I32 = new ThriftType("i32", BASIC_TYPE, "int", "Integer");
	public static final ThriftType I64 = new ThriftType("i64", BASIC_TYPE, "long", "Long");
	public static final ThriftType DOUBLE = new ThriftType("double", BASIC_TYPE, "double", "Double");
	public static final ThriftType STRING = new ThriftType("string", BASIC_TYPE, "String", "String");
	public static final ThriftType LIST = new ThriftType("list", COLLECTION_TYPE, "List");
	public static final ThriftType SET = new ThriftType("set", COLLECTION_TYPE, "Set");
	public static final ThriftType MAP = new ThriftType("map", COLLECTION_TYPE, "Map");
	public static final ThriftType ENUM = new ThriftType("enum", ENUM_TYPE, "enum", "Enum");
	public static final ThriftType VOID = new ThriftType("void", VOID_TYPE, "void", "Void");
	public static final ThriftType STRUCT = new ThriftType("struct", STRUCT_TYPE, "class", "Class");
	//
	public ThriftType() {
		super();
	}
	private ThriftType(String value, int type, String javaTypeName) {
		this(value,type,javaTypeName,null);
	}
	private ThriftType(String value, int type, String javaTypeName, String warpperClassName) {
		this.value = value;
		this.type = type;
		//
		this.javaTypeName = javaTypeName;
		this.warpperClassName = warpperClassName;
	}
	@Override
	public ThriftType clone() {
		try {
			return (ThriftType) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("clone object error!", e);
		}
	}
	public static ThriftType fromJavaType(Type type) {
		Class<?> clazz = null;
		if (type instanceof ParameterizedType) {
			clazz = (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			clazz = (Class<?>) type;
		}
		return fromJavaType(clazz);
	}
	public static ThriftType fromJavaType(Class<?> clazz) {
		if (clazz == short.class || clazz == Short.class) {
			return I16;
		}
		if (clazz == int.class || clazz == Integer.class) {
			return I32;
		}
		if (clazz == long.class || clazz == Long.class) {
			return I64;
		}
		if (clazz == String.class) {
			return STRING;
		}
		if (clazz == boolean.class || clazz == Boolean.class) {
			return BOOL;
		}
		if (clazz == Date.class) {
			return I64;
		}
		if (List.class.isAssignableFrom(clazz)) {
			return LIST;
		}
		if (Set.class.isAssignableFrom(clazz)) {
			return SET;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return MAP;
		}
		if (clazz.isEnum()) {
			return ENUM;
		}
		if (!clazz.getName().startsWith("java.lang")) {
			ThriftType thriftType = STRUCT.clone();
			thriftType.value = clazz.getSimpleName();
			return thriftType;
		}
		throw new RuntimeException("Unkonw type :" + clazz);
	}
	/**
	 * 
	 * @return
	 */
	public String getTypeName() {
		if (isBasicType() || isCollection()) {
			return value;
		}
		if (isEnum()) {
			return "enum";
		}
		if (isStruct()) {
			return "struct";
		}
		return "unkonw";
	}
	public boolean isBasicType() {
		return (this.type & BASIC_TYPE) == BASIC_TYPE;
	}
	public boolean isCollection() {
		return (this.type & COLLECTION_TYPE) == COLLECTION_TYPE;
	}
	public boolean isStruct() {
		return (this.type & STRUCT_TYPE) == STRUCT_TYPE;
	}
	public boolean isEnum() {
		return (this.type & ENUM_TYPE) == ENUM_TYPE;
	}
}
