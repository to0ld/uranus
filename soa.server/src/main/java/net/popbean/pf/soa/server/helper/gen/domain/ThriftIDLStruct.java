package net.popbean.pf.soa.server.helper.gen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ThriftIDLStruct implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6153012195148944728L;
	public Class<?> clazz;
	public String packageName;
	public List<ThriftStruct> structs = new ArrayList<ThriftStruct>();
	public List<ThriftEnum> enums = new ArrayList<ThriftEnum>();
	public List<ThriftService> services = new ArrayList<ThriftService>();
	/**
	 * 
	 */
	public ThriftIDLStruct(){
		super();
	}
	/**
	 * 
	 * @param clazz
	 */
	public ThriftIDLStruct(Class<?> clazz){
		super();
		this.clazz = clazz;
	}
	public ThriftIDLStruct mergeField(ThriftField tf){
		int size = this.structs.size();
		this.structs.get(size-1).fields.add(tf);
		return this;
	}
	public ThriftIDLStruct mergeMethodArg(ThriftMethodArg value){
		int size = this.services.size();
		int size_m = this.services.get(size-1).methods.size();
		this.services.get(size-1).methods.get(size_m-1).methodArgs.add(value);
		return this;
	}
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static ThriftIDLStruct build(Class<?> clazz){
		ThriftIDLStruct ret = new ThriftIDLStruct(clazz);
		//
		return ret;
	}
}
