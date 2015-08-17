package net.popbean.pf.nf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.nf.service.NodeFactoryBusinessService;
import net.popbean.pf.nf.vo.NodeFactoryModel;
import net.popbean.pf.security.vo.SecuritySession;
@Service("service/pf/node-factory")
public class NodeFactoryBusinessServiceImpl extends AbstractBusinessService implements NodeFactoryBusinessService {
	@Autowired
	@Qualifier("service/pf/bill/model/redis")
	BillModelBusinessService bmService;
	@Override
	public NodeFactoryModel find(String code, SecuritySession client) throws BusinessError {
		try {
			StringBuilder sql = new StringBuilder("select * from pb_bd_nf where code=${code}");
			NodeFactoryModel model = _commondao.find(sql, JO.gen("code",code),NodeFactoryModel.class, "没有找到编号为"+code+"的节点模型");
			BillModel bm = bmService.find(model.bill_code, null, false, client);
			model.model = bm;
			return model;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
