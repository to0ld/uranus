package net.popbean.pf.soa.server;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.popbean.pf.soa.client.ServerIpResolve;
import net.popbean.pf.soa.client.impl.ServerIpLocalNetworkResolve;
import net.popbean.pf.zk.ThriftException;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 服务端注册服务工厂
 */
public class ThriftServiceMultiplexedServerFactory implements InitializingBean {
	@Autowired
	private ApplicationContext _appctx;

	// 服务注册本机端口
	private Integer port = 8299;
	// 优先级
	private Integer weight = 1;// default
	
	// 服务版本号
	private String version;
	// 解析本机IP
	private ServerIpResolve serverIpResolve;
	// 服务注册
	private ServiceRegister serviceRegister;

	private ServerThread serverThread;

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setThriftServerIpResolve(ServerIpResolve thriftServerIpResolve) {
		this.serverIpResolve = thriftServerIpResolve;
	}

	public void setThriftServerAddressRegister(ServiceRegister thriftServerAddressRegister) {
		this.serviceRegister = thriftServerAddressRegister;
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void reg() throws Exception {
		// 解析ip
		if (serverIpResolve == null) {
			serverIpResolve = new ServerIpLocalNetworkResolve();
		}
		String serverIP = serverIpResolve.getServerIp();
		if (StringUtils.isEmpty(serverIP)) {
			throw new ThriftException("cant find server ip...");
		}

		String hostname = serverIP + ":" + port + ":" + weight;

		// 扫描出service
		Map<String, Object> beans = _appctx.getBeansWithAnnotation(Service.class);
		Iterator<Entry<String, Object>> iter = beans.entrySet().iterator();
		//
		TMultiplexedProcessor p = new TMultiplexedProcessor();
		//
		Map<String,Object> thirft_service = new HashMap<String, Object>();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			Object value = entry.getValue();
			TProcessor tp = buildProcessor(value);
			if (tp != null) {
//				String bean_alias = entry.getKey();entry.getValue().getClass().getName()
				String bean_alias = entry.getValue().getClass().getName();
				thirft_service.put(bean_alias, entry.getValue());
				p.registerProcessor(bean_alias, tp);
			}
			//
			
		}
		//
		// 需要单独的线程,因为serve方法是阻塞的.
		serverThread = new ServerThread(p, port);
		serverThread.start();
		// 注册服务
		iter = thirft_service.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			Object value = entry.getValue();
			Class<?> sc = value.getClass();
			if(sc != null && serviceRegister != null){
				serviceRegister.register(sc.getName(), version, hostname);
			}
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private TProcessor buildProcessor(Object inst) throws Exception {
		Class<?> serviceClass = inst.getClass();
		// 获取实现类接口
		Class<?>[] interfaces = serviceClass.getInterfaces();
		TProcessor processor = null;
		
		if (interfaces.length == 0) {
//			throw new IllegalClassFormatException("service-class should implements Iface");
			return processor;
		}
		
		String serviceName = null;
		for (Class<?> clazz : interfaces) {
			String cname = clazz.getSimpleName();
			if (!cname.equals("Iface")) {// 找到iface，找processor
				continue;
			}
			serviceName = clazz.getEnclosingClass().getName();
			String pname = serviceName + "$Processor";
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Class<?> pclass = classLoader.loadClass(pname);
			if (!TProcessor.class.isAssignableFrom(pclass)) {
				continue;
			}
			Constructor<?> constructor = pclass.getConstructor(clazz);
			processor = (TProcessor) constructor.newInstance(inst);
			return processor;
		}
		return processor;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.reg();
	}

	public void close() {
		serverThread.stopServer();
	}
}
