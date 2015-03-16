package net.popbean.pf.entity.helper;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.model.helper.EntityModelHelper;
import net.popbean.pf.exception.ErrorBuilder;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

public class VOHelper {
	private static  Map<String,Map<String,Field>> _pojo_struct_cache = new ConcurrentHashMap<>();
	/**
	 * 将value转化为指定的类型
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static <T> T  cast(Class<T> clazz,Object value){
		if(clazz.equals(String.class)){
			return (T)value;
		}else if(clazz.equals(Double.class)){
			return (T)TypeUtils.castToDouble(value);
		}else if(clazz.equals(Date.class)){
			return (T)TypeUtils.castToDate(value);
		}else if(clazz.equals(Timestamp.class)){
			return (T)TypeUtils.castToTimestamp(value);
		}else if(clazz.equals(Enum.class)){//如果有性能问题那就上cache吧enum应该不会太多
			return TypeUtils.castToEnum(value, clazz, null);
		}
		return (T)value;
	}
 
	/**
	 * 
	 * @param model
	 * @param vo
	 * @throws Exception
	 */
	public static void validate(EntityModel model,IValueObject vo)throws Exception{
		if(model == null){
			throw new Exception("传入参数错误(tm空值)");
		}
		if(vo == null){
			throw new Exception("传入参数错误(vo空值)");
		}
		JSONObject jo = JOHelper.vo2jo(vo);
		validate(model, jo);
		//
		return ;
	}
	public static void validate(EntityModel model,JSONObject jo)throws Exception{
		if(model == null){
			throw new Exception("传入参数错误(tm空值)");
		}
		if(JOHelper.isEmpty(jo)){
			throw new Exception("传入参数错误(jo空值)");
		}
		int pos = 0;
		List<FieldModel> list = model.field_list;
		
		FieldModel pk_field = model.findPK();
		//
		for(FieldModel f:list){
			if(f == null){
				ErrorBuilder.createSys().msg(model.code+"中的第"+pos+"位数据为空(field.size="+model.field_list.size()+")").execute();
			}
			//不是主键 并且 必填 并且 空值
			if(pk_field!=null && !f.code.equals(pk_field.code)){//不是主键
				//非空 但值为空
				if(f.isRequired() && StringUtils.isBlank(jo.getString(f.code))){
					if(f.defaultValue == null){
						ErrorBuilder.createBusiness().msg("必填项["+model.code+"."+f.name+"("+f.code+")]为空").execute();
					}else{
						jo.put(f.code, f.defaultValue);//确保填补默认值		
					}
				}
			}
			if(f.type == Domain.Code || f.type == Domain.Memo || f.type == Domain.PK){
				int maxLen = f.length;//((StringField)f).getMaxLen();
				String v = jo.getString(f.code);
				if(v!=null && v.length()>maxLen && f.code.lastIndexOf("_")==-1){//如果是ID_这样的，就不做任何处理
					throw new Exception("["+f.code+"]的值：["+v+"]超长");
				}				
			}
			//类型转化
			if(JOHelper.has(f, jo)){
				jo.put(f.code,jo.get(f));
			}
			pos++;
		}
		//
		return ;
	}
	public static boolean has(FieldModel field,IValueObject vo)throws Exception{
		Object value =get(vo, field.code);
		if(value != null){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param target
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	public static void set(Object target, String key, Object value) throws Exception{
		Field f = find(target,key);
		if(f == null){
			return ;
		}
		if(f.getType().isEnum()){//如果这个属性是enum就得特殊处理
			Class<Enum> ec = (Class<Enum>) f.getType();
			f.set(target,Enum.valueOf(ec, value.toString()));
		}else{
			f.set(target, value);	
		}
		
	}
	/**
	 * 
	 * @param target
	 * @param key
	 * @return
	 */
	private static Field find(Object target,String key){
		if(target == null){
			return null;
		}
		String clazz = target.getClass().getName();
		Map<String,Field> field_bus = _pojo_struct_cache.get(clazz);
		if(CollectionUtils.isEmpty(field_bus)){//没有就初始化,初期就不加锁了
			Field[] field_list = target.getClass().getDeclaredFields();
			Map<String,Field> bus = new HashMap<>();
			for(Field f:field_list){
				f.setAccessible(true);
				bus.put(f.getName(), f);
			}
			_pojo_struct_cache.put(clazz, bus);
			field_bus = bus;
		}
		Field f = field_bus.get(key);
		return f;
	}
	/**
	 * 
	 * @param target
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static Object get(Object target, String key) throws Exception{
		Field f = find(target,key);
		if(f == null){
			return null;
		}
		return f.get(target);
	}
	/**
	 * 将list1与list2的交集找出来(以list1为主体)
	 * @param list1
	 * @param list2
	 * @param pk_key1
	 * @param pk_key2
	 * @return
	 */
	public static List<JSONObject> in(List<JSONObject> list1,List<JSONObject> list2,String pk_key1,String pk_key2){
		return rlt(list1, list2, pk_key1, pk_key2, true);
	}
	/**
	 * 将list1中list2的内容剔除掉
	 * @param list1
	 * @param list2
	 * @param pk_key1
	 * @param pk_key2
	 * @return
	 */
	public static List<JSONObject> notIn(List<JSONObject> list1,List<JSONObject> list2,String pk_key1,String pk_key2){
		return rlt(list1,list2,pk_key1,pk_key2,false);
	}
	public static List<JSONObject> rlt(List<JSONObject> list1,List<JSONObject> list2,String pk_key1,String pk_key2,Boolean hasIn){
		List<JSONObject> ret = new ArrayList<>();
		if(CollectionUtils.isEmpty(list1) || CollectionUtils.isEmpty(list2)){
			if(hasIn){
				return ret;
			}
			if(!CollectionUtils.isEmpty(list1)){
				return list1;
			}
			if(!CollectionUtils.isEmpty(list2)){
				return list2;
			}
		}

		Map<String,String> keybus = new HashMap<>();
		for(JSONObject vv:list2){//构建list2的唯一标识集合
			keybus.put(vv.getString(pk_key2), vv.getString(pk_key2));
		}
		for(JSONObject v:list1){
			if(hasIn){
				if(keybus.containsKey(v.get(pk_key1))){
					ret.add(v);
				}	
			}else{
				if(!keybus.containsKey(v.get(pk_key1))){
					ret.add(v);
				}				
			}
		}
		return ret;
	}
	/**
	 * @param key
	 * @param vo
	 * @return
	 */
	public static boolean has(String key,JSONObject vo){
		if(vo == null){
			return false;
		}
		if(StringUtils.isBlank(key)){
			return false;
		}
		if(vo.get(key)!=null){
			return true;
		}
		return false;
	}
	/**
	 * 从ref中获得主键
	 * @param ref_value
	 * @return
	 */
	public static String getPKFromRef(String ref_value){
		return ref_value.split(EntityModelHelper.REF_SPLIT)[0];
	}
	/**
	 * 
	 * @param pk
	 * @param name
	 * @return
	 */
	public static String buildRef(String pk,String name){
		return pk+EntityModelHelper.REF_SPLIT+name;
	}
}
