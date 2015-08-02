package net.popbean.pf.srv.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorCate;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.PermVO;
import net.popbean.pf.security.vo.RltRolePerm;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/perm")
public class PermSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {

	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	@Override
	public String imp(String version, JSONObject param, SecuritySession client) throws BusinessError {
		try{
			String appkey = param.getString("app.code");
			//得到自己的注册信息，得到前缀
			StringBuilder sql = new StringBuilder(" select * from pb_bd_app where code=${APP_CODE}");
			AppVO app_inst = _commondao.find(sql, JO.gen("app_code",appkey),AppVO.class, "没有找到appkey="+appkey);
			//
			//拼凑连接，进行读取
			String path = "classpath:/data/app/"+appkey+"/install/perm/cfg.data";//如果是安装的情况
			if(StringUtils.isBlank(path)){
				path = "classpath:/data/app/"+appkey+"/patch/"+version+"/perm/cfg.data";
			} 
			String content = IOHelper.readByChar(path, "utf-8");
			JSONObject jo = JSON.parseObject(content);
			JSONArray children = jo.getJSONArray("data");
//			List<PermVO> children = JSON.parseArray(jo.getJSONArray("data").toJSONString(), PermVO.class);
			
			//
			String pk_app = app_inst.id;
			String pk_app_code = app_inst.code;
			String pk_app_name = app_inst.name;
			//从app中的到pk_org_crt信息
			if(CollectionUtils.isEmpty(children)){
				throw new BusinessError("传入的数据为空，无法导入",ErrorCate.BUSINESS);
			}
			
			//补贴创建企业信息
			List<PermVO> insert_list = new ArrayList<>();
			for(int i=0;i<children.size();i++){
				JSONObject p = children.getJSONObject(i);
				//
				PermVO pvo = JSON.parseObject(p.toJSONString(), PermVO.class);
				pvo.app_code = appkey;
				pvo.id = IdGenHelper.genID(appkey,pvo.code);
				insert_list.add(pvo);				
				//
				if(JOHelper.equalsStringValue(p, "type", "3")){//3 == folder
					JSONArray p_children = p.getJSONArray("children");
					for(int ii=0;ii<p_children.size();ii++){
						JSONObject pp = p_children.getJSONObject(ii);
						PermVO childvo = JSON.parseObject(p.toJSONString(), PermVO.class);
						childvo.id = IdGenHelper.genID(appkey,childvo.code);
						//补齐folder的数据
						childvo.folder_id = IdGenHelper.genID(appkey,childvo.code);
						childvo.app_code = appkey;
						insert_list.add(childvo);
					}
				}
			}
			//先删除历史数据
//			sql = new StringBuilder("delete from pb_bd_perm where app_code=${app_code}");
//			_commondao.executeChange(sql, JO.gen("app_code",appkey));
//			_commondao.batchInsert(insert_list);
			_commondao.batchReplace(insert_list);
			//构建一下默认角色以及该角色的权限
			rlt(appkey);
		}catch(Exception e){
			processError(e);
		}
		return null;
	}
	/**
	 * 用于建立默认的应用管理员角色以及构建最大的角色
	 * @throws BusinessError
	 */
	private void rlt(String appkey)throws BusinessError{
		try {
			String role_id = appkey+":admin";
			//FIXME 1-为应用创建一个管理员角色:${appkey}_admin，主键为${app_key}:admin
			//FIXME 2-为该角色映射该应用中所有的权限(先删除后插入)
			StringBuilder sql = new StringBuilder("delete from rlt_role_perm where role_id=${ROLE_ID}");
			_commondao.executeChange(sql, JO.gen("role_id",role_id));
			//
			//
			sql = new StringBuilder("select * from pb_bd_perm where app_code=${APP_CODE}");
			List<PermVO> perm_list = _commondao.query(sql, JO.gen("app_code",appkey),PermVO.class);
			List<RltRolePerm> insert_list = new ArrayList<>();
			for(PermVO perm:perm_list){
				RltRolePerm inst = new RltRolePerm();
				inst.perm_id = perm.id;
				inst.id = IdGenHelper.genID(role_id,perm.id);
				if(perm.scope == 0){
					inst.role_id = role_id;	
				}
				insert_list.add(inst);
			}
			_commondao.batchInsert(insert_list);//其实采用替换更好，但问题在于谁知道更新里到底干啥了，改一个原来的链接，将节点指向新的位置。。。所以还是这样省事
		} catch (Exception e) {
			processError(e);
		}
	}
}
