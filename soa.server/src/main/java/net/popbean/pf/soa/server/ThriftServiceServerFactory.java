package net.popbean.pf.soa.server;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;

import net.popbean.pf.soa.client.ServerIpResolve;
import net.popbean.pf.soa.client.impl.ServerIpLocalNetworkResolve;
import net.popbean.pf.zk.ThriftException;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;


/**
 * 服务端注册服务工厂
 */
public class ThriftServiceServerFactory implements InitializingBean {
	// 服务注册本机端口
	private Integer port = 8299;
	// 优先级
	private Integer weight = 1;// default
	// 服务实现类
	private Object service;// serice实现类
	//服务版本号
	private String version;
	// 解析本机IP
	private ServerIpResolve serverIpResolve;
	//服务注册
	private ServiceRegister serviceRegister;

	private ServerThread serverThread;
	
	public void setPort(Integer port) {
		this.port = port;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setService(Object service) {
		this.service = service;
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

	@Override
	public void afterPropertiesSet() throws Exception {
		if (serverIpResolve == null) {
			serverIpResolve = new ServerIpLocalNetworkResolve();
		}
		String serverIP = serverIpResolve.getServerIp();
		if (StringUtils.isEmpty(serverIP)) {
			throw new ThriftException("cant find server ip...");
		}

		String hostname = serverIP + ":" + port + ":" + weight;

		//
		Class<?> serviceClass = service.getClass();
		// 获取实现类接口
		Class<?>[] interfaces = serviceClass.getInterfaces();
		if (interfaces.length == 0) {
			throw new IllegalClassFormatException("service-class should implements Iface");
		}
		//
//		TMultiplexedProcessor p = new TMultiplexedProcessor();
//		p.registerProcessor("", null);
		//
		// reflect,load "Processor";
		TProcessor processor = null;
		String serviceName = null;
		for (Class<?> clazz : interfaces) {
			String cname = clazz.getSimpleName();
			if (!cname.equals("Iface")) {//找到iface，找processor
				continue;
			}
			serviceName = clazz.getEnclosingClass().getName();
			String pname = serviceName + "$Processor";
			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				Class<?> pclass = classLoader.loadClass(pname);
				if (!TProcessor.class.isAssignableFrom(pclass)) {
					continue;
				}
				Constructor<?> constructor = pclass.getConstructor(clazz);
				processor = (TProcessor) constructor.newInstance(service);
				break;
			} catch (Exception e) {
				//
			}
		}
		if (processor == null) {
			throw new IllegalClassFormatException("service-class should implements Iface");
		}
		//需要单独的线程,因为serve方法是阻塞的.
		serverThread = new ServerThread(processor, port);
		serverThread.start();
		// 注册服务
		if (serviceRegister != null) {
			serviceRegister.register(serviceName, version, hostname);
		}

	}	
	public void close() {
		serverThread.stopServer();
	}
}
