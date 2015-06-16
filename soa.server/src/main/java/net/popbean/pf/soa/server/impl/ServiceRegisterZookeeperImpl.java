package net.popbean.pf.soa.server.impl;

import java.io.UnsupportedEncodingException;

import net.popbean.pf.soa.server.ServiceRegister;
import net.popbean.pf.zk.ThriftException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 注册服务列表到zk
 */
public class ServiceRegisterZookeeperImpl implements ServiceRegister {

	private CuratorFramework zkClient;

	public ServiceRegisterZookeeperImpl() {
	}

	public ServiceRegisterZookeeperImpl(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	@Override
	public void register(String service, String version, String address) {
		if (zkClient.getState() == CuratorFrameworkState.LATENT) {
			zkClient.start();
		}
		if (StringUtils.isEmpty(version)) {
			version = "1.0.0";
		}
		// 临时节点
		try {
			String path = "/" + service + "/" + version + "/" + address;
			zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		} catch (UnsupportedEncodingException e) {
			throw new ThriftException("register service address to zookeeper exception: address UnsupportedEncodingException", e);
		} catch (Exception e) {
			throw new ThriftException("register service address to zookeeper exception:{}", e);
		}
	}

	public void close() {
		zkClient.close();
	}
}
