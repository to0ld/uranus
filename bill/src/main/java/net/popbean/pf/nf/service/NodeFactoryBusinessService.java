package net.popbean.pf.nf.service;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.nf.vo.NodeFactoryModel;
import net.popbean.pf.security.vo.SecuritySession;

public interface NodeFactoryBusinessService {
	/**
	 * 活动节点工厂的模型
	 * @param code
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	public NodeFactoryModel find(String code, SecuritySession client) throws BusinessError;
}
