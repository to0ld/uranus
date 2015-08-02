package net.popbean.pf.srv.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.persistence.vo.BatchVO;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;
/**
 * 
 * @author to0ld
 *
 */
@Service("service/pf/srv/persistencebatch")
public class PersistenceBatchSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	//
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	public String imp(String version, JSONObject param,SecuritySession client) throws BusinessError {
		try {
			esService.syncDbStructByClazz(Arrays.asList(EntityModel.class.getName(),FieldModel.class.getName(),BatchVO.class.getName()), client);
//			esService.syncDbStructByClazz(Arrays.asList(EntityMeta.class.getName(),FieldMeta.class.getName(),BatchMeta.class.getName()), null);//建表
			//清除mt_pf_batch的记录，插入一条记录
			StringBuilder sql = new StringBuilder("delete from pb_pf_batch");
			_commondao.executeChange(sql, null);
			BatchVO p = new BatchVO();
			_commondao.save(p);
			return null;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
