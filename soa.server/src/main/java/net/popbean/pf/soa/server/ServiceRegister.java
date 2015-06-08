package net.popbean.pf.soa.server;

/**
 * 
 */
public interface ServiceRegister {
	/**
	 * 发布服务接口
	 */
	void register(String service,String version,String address);
}
