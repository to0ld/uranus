package net.popbean.pf.soa.client.multiplexed;

import java.net.InetSocketAddress;

import net.popbean.pf.soa.client.PoolOperationCallBack;
import net.popbean.pf.soa.client.ThriftServerAddressProvider;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * 连接池,thrift-client for spring
 */
public class ThriftMultiplexedClientPoolFactory extends BasePooledObjectFactory<TServiceClient> {

	private final ThriftServerAddressProvider serverAddressProvider;
	private final TServiceClientFactory<TServiceClient> clientFactory;
	private PoolOperationCallBack callback;

	public ThriftMultiplexedClientPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory) throws Exception {
		this.serverAddressProvider = addressProvider;
		this.clientFactory = clientFactory;
	}

	public ThriftMultiplexedClientPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory, PoolOperationCallBack callback) throws Exception {
		this.serverAddressProvider = addressProvider;
		this.clientFactory = clientFactory;
		this.callback = callback;
	}

	@Override
	public void destroyObject(PooledObject<TServiceClient> client) throws Exception {
		if (callback != null) {
			try {
				callback.destroy(client.getObject());
			} catch (Exception e) {
				// logger.warn("destroyObject:{}", e);
			}
		}
		// logger.info("destroyObject:{}", client);
		TTransport pin = client.getObject().getInputProtocol().getTransport();
		pin.close();
		TTransport pout = client.getObject().getOutputProtocol().getTransport();
		pout.close();
	}

	@Override
	public void activateObject(PooledObject<TServiceClient> client) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<TServiceClient> client) throws Exception {
	}

	@Override
	public boolean validateObject(PooledObject<TServiceClient> client) {
		TTransport pin = client.getObject().getInputProtocol().getTransport();
		// logger.info("validateObject input:{}", pin.isOpen());
		TTransport pout = client.getObject().getOutputProtocol().getTransport();
		// logger.info("validateObject output:{}", pout.isOpen());
		return pin.isOpen() && pout.isOpen();
	}

	@Override
	public PooledObject<TServiceClient> makeObject() throws Exception {
		InetSocketAddress address = serverAddressProvider.selector();
		TSocket tsocket = new TSocket(address.getHostName(), address.getPort());
		TTransport transport = new TFramedTransport(tsocket);
		TProtocol protocol = new TBinaryProtocol(transport);
		// 采用
		TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, serverAddressProvider.getService());
		//
		TServiceClient client = this.clientFactory.getClient(mp);
		transport.open();
		if (callback != null) {
			try {
				callback.make(client);
			} catch (Exception e) {
				// logger.warn("makeObject:{}", e);
			}
		}
		return new DefaultPooledObject<TServiceClient>(client);
	}

	@Override
	public TServiceClient create() throws Exception {
		return null;
	}

	@Override
	public PooledObject<TServiceClient> wrap(TServiceClient obj) {
		return null;
	}

}
