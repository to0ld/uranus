package net.popbean.pf.soa.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

public class ServerThread extends Thread {
	private TServer server;

	ServerThread(TProcessor processor, int port) throws Exception {
		TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
		TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
		TProcessorFactory processorFactory = new TProcessorFactory(processor);
		tArgs.processorFactory(processorFactory);
		tArgs.transportFactory(new TFramedTransport.Factory());
		tArgs.protocolFactory(new TBinaryProtocol.Factory(true, true));
		server = new TThreadedSelectorServer(tArgs);
	}

	@Override
	public void run() {
		try {
			server.serve();
		} catch (Exception e) {
			//
		}
	}

	public void stopServer() {
		server.stop();
	}
}
