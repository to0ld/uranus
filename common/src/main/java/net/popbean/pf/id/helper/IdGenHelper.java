package net.popbean.pf.id.helper;

import org.apache.commons.lang3.StringUtils;

import net.popbean.pf.exception.BusinessError;

public class IdGenHelper {
	public static String genID(String... keys)throws BusinessError{
		//主键格式为：表名.字段名.补零
		StringBuilder ret = new StringBuilder();
		//
		int pos = 0;
		for(String key:keys){
			if(pos!=0){
				ret.append(":");
			}
			if(!StringUtils.isBlank(key)){
				ret.append(key);
				pos++;
			}
		}
		return ret.toString();
	}
}
