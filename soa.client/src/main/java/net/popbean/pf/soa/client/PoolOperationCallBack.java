package net.popbean.pf.soa.client;

import org.apache.thrift.TServiceClient;

public interface PoolOperationCallBack {
	// 销毁client之前执行
	void destroy(TServiceClient client);

	// 创建成功是执行
	void make(TServiceClient client);
}
