package net.popbean.pf.app.service.impl;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.app.helper.AppHelper;
import net.popbean.pf.app.service.AppManagementBusinessService;
import net.popbean.pf.app.vo.AppConfigVO;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;
import net.popbean.pf.srv.vo.SrvConfigVO;
@Service("service/pf/am")
public class AppManagementBusinessServiceImpl extends AbstractBusinessService implements AppManagementBusinessService {
	@Autowired
	private ApplicationContext appctx;
	
	/**
	 * 
	 */
	public void install(String appkey, SecuritySession client) throws BusinessError {
		try {
			
			//FIXME 读取配置文件
			AppConfigVO cfg_inst = AppHelper.loadConfig(appkey,null);
			if(cfg_inst == null){//没有读取到配置文件就算求，但应该给出异常提示信息，以便定位问题
				return ;
			}
			//------------------------------------------------------
			List<String> step_list= cfg_inst.steps;
			if(CollectionUtils.isEmpty(step_list)){
				return ;
			}
			String skip_msg = "skip step:";
			for(String step:step_list){
				String[] parts = step.split("\\?");
				JSONObject p = new JSONObject();
				//为了世界和平，将app的信息也带入;为了避免冲突予以变更:code -> app.code;badge ->app.badge
				p.put("app.code", cfg_inst.code);
				p.put("app.badge", cfg_inst.badge);
				p.put("app.org_code",cfg_inst.org_code);
				p.put("app.id", IdGenHelper.genID(cfg_inst.org_code,cfg_inst.code));//app_id -> org_code:app_code
				p.put("app.name",cfg_inst.name);
				p.put("app.memo",cfg_inst.memo);
				p.put("app.secret",cfg_inst.secret);
				//
				if(parts.length >1){//说明有参数
					String[] pairs = parts[1].split("&");
					for(String pair:pairs){
						String[] tmp = pair.split("=");
						if(tmp.length == 2){
							p.put(tmp[0], tmp[1]);
						}
					}
				}
				if(JOHelper.equalsStringValue(p, "skip", "true")){
					continue;
				}
				String clazz = parts[0];
				SrvManagementBusinessService smService = null;
				smService = appctx.getBean(clazz, SrvManagementBusinessService.class);//很有可能没拿到
//				try {
//						
//				} catch (BeansException e) {//选择沉默，继续执行？其实不太好
//					// TODO: handle exception
//				}
				
				if(smService == null){
					return ;
				}
				smService.imp(null, p,client);
			}
			//FIXME 写入oplog
			//------------------------------------------------------
			//FIXME 分为：应用维护(安装、升级)；企业建账两大部分
			//FXIME 1-
			//一个应用的安装过程：该过程体现在配置文件中(将企业帐套的信息予以分离)
			//FIXME 1-建表结构
			//FIXME 2-节点导入(nodefactory之类的东西)
			//FIXME 3-权限对象，授权信息
			//FIXME 坚决汲取教训不能写死，要用配置文件
			
			//FIXME 归入企业建账的环节，予以保留
//			if(appkey.equals("pf")){//app info信息(url base可以先指定，然后再修改？)
//				makePfAppInfo(client);
//			}else{//app应该是先注册，拿到appkey以及secret然后才能部署的模式
////				impAppInfo(client);
//			}
			//导入功能节点
//			impPerm(appkey,client);//如果要单项，可以开放为方法
			
			//------------------------------------------------------默认用户、角色、授权(默认的不予提供所属信息?)---------
			//应该予以分开；角色；角色-权限跟随应用；用户；用户-角色跟随企业建账
			/*
			//生成默认角色:
			//业务口径的应用级别管理员：{appkey}_admin
			//业务口径的应用级别普通用户:{appkey}_emp
			StringBuilder sql = new StringBuilder ("select 1 from pb_bd_role where role_code=${ROLE_CODE} and pk_org_crt=${PK_ORG_CRT}");
			//
			List<String> key_list = Arrays.asList("admin","emp");
			List<String> name_list = Arrays.asList("管理员","普通用户");
			//
			if(appkey.equals("pf")){//如果是pf的app还需要插入一个最牛逼的admin角色
				key_list = Arrays.asList("admin");
				name_list = Arrays.asList("管理员");
			}
			for(int i=0,len=key_list.size();i<len;i++){
				VO role = VO.gen("ROLE_CODE",appkey+"_"+key_list.get(i),"ROLE_NAME",appkey+name_list.get(i),"ISTAT",3,"PK_ORG_CRT","root");
				role.set("APP_CODE",appkey);
				String pk_role = IdGenHelper.genID("root", appkey+"_"+key_list.get(i));//root.admin
				
				boolean flag = _commondao.checkUnique(sql, VO.gen("ROLE_CODE",appkey+"_"+key_list.get(i),"PK_ORG_CRT","root"));
				if(flag){
					_commondao.batchReplace(RoleMeta.class, Arrays.asList(role));

				}
			}
			//生成角色授权数据
			grant(appkey);
			*/
			//----------------------------------------------------------------------------


			//导入nodefactory的节点数据
//			impNode(appkey);
		} catch (Exception e) {
			processError(e);
		}
	}

	public void unInstall(String appkey, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

	public void upgrade(String appkey, String version, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

	public void rollback(String appkey, String version, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

}
