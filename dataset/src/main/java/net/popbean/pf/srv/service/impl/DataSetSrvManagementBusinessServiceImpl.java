package net.popbean.pf.srv.service.impl;

import java.util.Arrays;
import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.dataset.vo.DataSetFieldModel;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.dataset.vo.SourceType;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/ds")
public class DataSetSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {

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
			String path = "classpath:/data/app/"+appkey+"/install/ds/cfg.data";//如果是安装的情况
			if(StringUtils.isBlank(path)){
				path = "classpath:/data/app/"+appkey+"/patch/"+version+"/ds/cfg.data";
			} 
			//
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			List<DataSetModel> list = JSON.parseArray(jo.getJSONArray("data").toJSONString(), DataSetModel.class);
			//
			//将dataset model 转化为datapod model(grid)
			//appendDatapodModel(list);
			//FIXME 如果太慢，就使用batch
			for(DataSetModel dm:list){
				String pk_ds = IdGenHelper.genID("root",dm.code);
				JSONObject p = JO.gen("id",pk_ds,"code",dm.code);
				//清除现有数据
				StringBuilder sql = new StringBuilder("delete from pb_pf_ds where code=${code}");
				_commondao.executeChange(sql, p);
				sql = new StringBuilder("delete from pb_pf_ds_field where id=${id}");
				_commondao.executeChange(sql, p);
				//插入
				dm.id = pk_ds;
				dm.account_crt_id = IdGenHelper.genID("root", "admin");
				dm.company_crt_id = "root";
				if(dm.src_type.equals(SourceType.sql) && StringUtils.isBlank(dm.exec_exp)){//eum算啥呢？本质上是sql，来玩默认的？不指定就创建一个？
					dm.exec_exp = "select * from pb_pf_ds_range where ds_id=${ds_id}";//ds_id -> {company_id}:{ds_code}
					
				}
				_commondao.batchReplace(Arrays.asList(dm));
				//
				List<DataSetFieldModel> dm_list = dm.field_list;
				//
				if(CollectionUtils.isEmpty(dm_list)){
					continue;
				}
				String account_crt_id = IdGenHelper.genID("root", "admin");
				for(DataSetFieldModel f:dm_list){
					f.ds_id = pk_ds;
					f.account_crt_id = account_crt_id;
					f.id = IdGenHelper.genID(dm.code,f.code);
				}
				_commondao.batchReplace(dm_list);
			}
			//FIXME 清除缓存
//			operations.dropCollection(MongoHelper.COLL_DP_MODEL);//
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
