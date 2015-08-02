package net.popbean.pf.srv.service.impl;

import java.util.Arrays;
import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.dataset.vo.DataSetFieldModel;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.dataset.vo.SourceType;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.rm.helper.ResourceMappingHelper;
import net.popbean.pf.rm.vo.ResourceMappingModel;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/rm")
public class ResourceMappingSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	//
	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	@Override
	public String imp(String version, JSONObject param, SecuritySession client) throws BusinessError {
		try {
			String appkey = param.getString("app.code");
//			String real_root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//			String file_path = real_root+"/data/patch/"+version+"/dataset/model.data";
			//
			String path = "classpath:/data/app/"+appkey+"/install/rm/cfg.data";//如果是安装的情况
			if(StringUtils.isBlank(path)){
				path = "classpath:/data/app/"+appkey+"/patch/"+version+"/rm/cfg.data";
			} 
			//
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			List<ResourceMappingModel> list = JSON.parseArray(jo.getJSONArray("data").toJSONString(), ResourceMappingModel.class);
			//
			//将dataset model 转化为datapod model(grid)
			//appendDatapodModel(list);
			//FIXME 如果太慢，就使用batch
			for(ResourceMappingModel dm:list){
				String rm_id = IdGenHelper.genID("root",dm.code);
				JSONObject p = JO.gen("id",rm_id,"code",dm.code);
				//清除现有数据
				StringBuilder sql = new StringBuilder("delete from pb_pf_rm where code=${code}");
				_commondao.executeChange(sql, p);
				//插入
				dm.id = rm_id;
				dm.account_crt_id = IdGenHelper.genID("root", "admin");
				dm.company_crt_id = "root";
				_commondao.batchReplace(Arrays.asList(dm));
				//建立表结构
				EntityModel em = ResourceMappingHelper.buildRelationEntityModel(dm.relation_code);
				esService.syncEntityModel(Arrays.asList(em), client);
			}
			//FIXME 清除缓存
//			operations.dropCollection(MongoHelper.COLL_DP_MODEL);//
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
