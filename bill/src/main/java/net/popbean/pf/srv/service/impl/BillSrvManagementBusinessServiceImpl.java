package net.popbean.pf.srv.service.impl;

import java.util.List;

import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/bill")
public class BillSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {
	@Autowired
	@Qualifier("service/pf/bill/model/redis")
	private BillModelBusinessService bmService;
	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	@Override
	public String imp(String version, JSONObject param, SecuritySession client) throws BusinessError {
		try {
			String appkey = param.getString("app.code");
			//
			String pref = "classpath:/data/app/"+appkey+"/install/";//如果是安装的情况
//			String path = "classpath:/data/app/"+appkey+"/install/bill/cfg.data";//如果是安装的情况
			if(!StringUtils.isBlank(version)){
				pref = "classpath:/data/app/"+appkey+"/patch/"+version+"/";
			} 
			String path = pref+"bill/cfg.data";
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			List<String> bill_list = JSON.parseArray(jo.getJSONArray("data").toJSONString(), String.class);
//			List<NodeFactoryInst> nodes = JSON.parseArray(jo.getJSONArray("data").toJSONString(), NodeFactoryInst.class);
			
			for(String bill_code:bill_list){
				String tmp_path = pref+"bill/"+bill_code+".data";
				content = IOHelper.readByChar(tmp_path, "utf-8");
				jo = JSON.parseObject(content);
				BillModel model = JSON.parseObject(jo.getJSONObject("data").toJSONString(), BillModel.class);
				bmService.save(model, client);
			}
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

}
