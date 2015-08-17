package net.popbean.pf.srv.service.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.RoleVO;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import com.alibaba.fastjson.JSONObject;
/**
 * 用于解决基础的安装问题
 * @author to0ld
 *
 */
@Service("service/pf/srv/app")
public class AppSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {

	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}

	@Override
	public String imp(String version, JSONObject param, SecuritySession client) throws BusinessError {
		try {
			//
			String pk_app = param.getString("app.id");//认为还是org_code:app_code比较合适
			//
			String app_code = param.getString("app.code");
			if(StringUtils.isBlank(version)){//设定为安装的情况
				//
				AppVO app = new AppVO();
//				app.id = pk_app;
				app.code = param.getString("app.code");
				app.name = param.getString("app.name");
				app.secret = param.getString("app.secret");
				app.memo = param.getString("app.memo");
				//
				_commondao.batchReplace(Arrays.asList(app));
//				_commondao.save(app, false, pk_app);
			}else{//如果是更新，就存在更新的可能
				StringBuilder sql = new StringBuilder("update pb_bd_app set app_badge=${BADGE} where id=${PK_APP}");
				_commondao.executeChange(sql, JO.gen("PK_APP",pk_app,"BADGE",param.getString("app.badge")));							
			}
			makeRole(app_code);
		} catch (Exception e) {
			processError(e);
		}
		//
		return null;
	}
	private void makeRole(String app_code)throws BusinessError{
		try {
			RoleVO inst = new RoleVO();
			inst.id = IdGenHelper.genID(app_code,app_code+"_admin");
			inst.code = app_code+"_admin";
			inst.app_code = app_code;
			inst.name = app_code+"管理员";
			_commondao.batchReplace(Arrays.asList(inst));			
		} catch (Exception e) {
			processError(e);
		}
	}
}
