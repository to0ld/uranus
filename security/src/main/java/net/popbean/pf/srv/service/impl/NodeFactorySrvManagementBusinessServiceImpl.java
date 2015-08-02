package net.popbean.pf.srv.service.impl;

import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.nf.vo.NodeFactoryInst;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/nf")
public class NodeFactorySrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {

	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	@Override
	public String imp(String version, JSONObject param, SecuritySession client) throws BusinessError {
		try {
			String appkey = param.getString("app.code");
			//
			StringBuilder sql = new StringBuilder(" select * from pb_bd_app where code=${APP_CODE}");
			AppVO app_inst = _commondao.find(sql, JO.gen("app_code",appkey),AppVO.class,"没有找到appkey="+appkey);
			//
			String path = "classpath:/data/app/"+appkey+"/install/nf/cfg.data";//如果是安装的情况
			if(!StringUtils.isBlank(version)){
				path = "classpath:/data/app/"+appkey+"/patch/"+version+"/nf/cfg.data";
			} 
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			List<NodeFactoryInst> nodes = JSON.parseArray(jo.getJSONArray("data").toJSONString(), NodeFactoryInst.class);
			
			for(NodeFactoryInst v:nodes){
				String id = IdGenHelper.genID(appkey,v.code);
				v.id = id;
			}
			_commondao.batchReplace(nodes);
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

}
