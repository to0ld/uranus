package net.popbean.pf.entity;
/**
 * 一次失败的尝试
 * @author to0ld
 *
 * @param <T>
 */
@Deprecated
public interface IValueObjectWrapper<T> {
	/**
	 * 
	 * @param target
	 * @param key
	 * @param value
	 */
	public void set(T target, String key, Object value)throws Exception;
	/**
	 * 
	 * @param target
	 * @param key
	 * @return
	 */
	public Object get(T target, String key)throws Exception;
}
