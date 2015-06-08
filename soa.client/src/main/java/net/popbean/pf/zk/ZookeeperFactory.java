package net.popbean.pf.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.PreDestroy;
/**
 * 
 */
@Component("zk/factory")
public class ZookeeperFactory implements FactoryBean<CuratorFramework> {
	@Value("${zk.hosts}")
	private String hosts;
	// session超时
	@Value("${zk.session.timeout}")
	private int sessionTimeout = 30000;
	@Value("${zk.conn.timeout}")
	private int connectionTimeout = 30000;

	// 共享一个zk链接
	private boolean singleton = true;

	// 全局path前缀,常用来区分不同的应用
	@Value("${zk.namespace}")
	private String namespace = "";

	private final static String ROOT = "net.popbean";

	private CuratorFramework zkClient;

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	@Override
	public CuratorFramework getObject() throws Exception {
		if (singleton) {
			if (zkClient == null) {
				zkClient = create();
				zkClient.start();
			}
			return zkClient;
		}
		return create();
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	public CuratorFramework create() throws Exception {
		if (StringUtils.isEmpty(namespace)) {
			namespace = ROOT;
		} else {
			namespace = ROOT +"/"+ namespace;
		}
		return create(hosts, sessionTimeout, connectionTimeout, namespace);
	}

	public static CuratorFramework create(String connectString, int sessionTimeout, int connectionTimeout, String namespace) {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		return builder.connectString(connectString)
				.sessionTimeoutMs(sessionTimeout)
				.connectionTimeoutMs(connectionTimeout)
				.canBeReadOnly(true)
				.namespace(namespace)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
				.defaultData(null).build();
	}
	@PreDestroy
	public void close() {
		if (zkClient != null) {
			zkClient.close();
		}
	}
}
