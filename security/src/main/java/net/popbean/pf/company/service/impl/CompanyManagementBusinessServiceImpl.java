package net.popbean.pf.company.service.impl;

import java.util.Arrays;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.company.service.CompanyManagementBusinessService;
import net.popbean.pf.company.vo.RltCompanyApp;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorCate;
import net.popbean.pf.id.helper.IdGenHelper;
import net.popbean.pf.mt.service.MultiTenantManagementBusinessService;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.AppVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.RltCompanyAccount;
import net.popbean.pf.security.vo.RltRoleAccount;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
@Service("service/company/management")
public class CompanyManagementBusinessServiceImpl extends AbstractBusinessService implements CompanyManagementBusinessService {
	@Autowired
	private ApplicationContext appctx;
	//
	/**
	 * 这个模式有个很重要的问题，只能当前登陆者帮忙创建,将来可以用加参数的办法解决运维代建的问题
	 * @param inst
	 * @param client
	 * @throws BusinessError
	 */
	@Override
	public void create(CompanyVO inst, SecuritySession client) throws BusinessError {
		try {
			//企业分为运维和租户两种；如果是运维需要判断是否已经存在
			if(inst.type == 3){
				StringBuilder v_sql = new StringBuilder("select 1 from pb_bd_company where type=3");
				_commondao.assertTrue(v_sql, JO.gen("code",inst.code), "已经存在编码为"+inst.code+"的运营企业,不能继续该操作(目前只能存在一个运营企业)");
			}else{
				//暂时不用做啥处理
			}
			//1- 创建企业(排除重名的可能性)
			StringBuilder sql = new StringBuilder("select 1 from pb_bd_company where code=${code} $[and id!=${id}]");
			boolean flag = _commondao.assertTrue(sql, JO.gen("code",inst.code,"id",inst.id), inst.code);
			if(flag){
				throw new BusinessError("重名",ErrorCate.BUSINESS);
			}
			String company_id = inst.id;
			if(StringUtils.isBlank(company_id)){//没企业主键的情况,保障主键是规则的
				company_id = _commondao.save(inst, false, inst.code);
			}else{
				_commondao.save(inst);
			}
			//2- 建立起创建者与该企业的联系

			if(client!=null){
				RltCompanyAccount rlt = new RltCompanyAccount();
				rlt.company_id = company_id;
				rlt.account_id = client.account_id;
				rlt.company_crt_id = client.company.id;
				rlt.id = IdGenHelper.genID(company_id,client.account_id);
				rlt.type = 3;
				_commondao.batchReplace(Arrays.asList(rlt));
			}
			
		} catch (Exception e) {
			processError(e);
		}
	}

	@Override
	public void activate(CompanyVO inst, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

	@Override
	public void seal(CompanyVO inst, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableApp(String company_code, String app_code, SecuritySession client) throws BusinessError {
		try {
			//找到app，确保丫存在
			StringBuilder sql = new StringBuilder("select 1 from pb_bd_app where code=${code}");
			AppVO result = _commondao.find(sql, JO.gen("code",app_code), AppVO.class, " 没有找到编码为"+app_code+"的应用");
			//读取配置文件:data/app
			
			//执行一个公用逻辑：将公司的管理员赋予该应用的最大权限
			sql = new StringBuilder("select * from pb_bd_company where code=${code}");
			CompanyVO inst = _commondao.find(sql, JO.gen("code",company_code), CompanyVO.class, "没有找到编码为"+company_code+"的企业");
			sql = new StringBuilder("select * from pb_bd_account where id in ( select a.account_id from rlt_company_account a where a.type=3 and company_id=${company_id})");
			AccountVO account = _commondao.find(sql, JO.gen("company_id",inst.id),AccountVO.class,"没有找到该企业的管理员");
			//
			RltRoleAccount rlt = new RltRoleAccount();
			rlt.account_id = account.id;
			rlt.role_id = IdGenHelper.genID(app_code,app_code+"_admin");
			rlt.id = IdGenHelper.genID(rlt.role_id,rlt.account_id);
			_commondao.batchReplace(Arrays.asList(rlt));
			
			//执行app提供的回调来执行该app的初始化过程:读取每个应用为建账所使用的回调(path:data/app/{appkey}/company/cfg.data)
			//FIXME 为了防止误伤，可以使用强制的策略：service/app/active/{appkey}没找到就是无需处理
//			AppConfigVO app_cfg = AppHelper.loadConfig(app.code, null);
//			String cb_clazz = app_cfg.mt;
//			if(!StringUtils.isBlank(cb_clazz)){
//				MultiTenantManagementBusinessService mtService = null;
//				mtService = appctx.getBean(cb_clazz, MultiTenantManagementBusinessService.class);//很有可能没拿到
//				mtService.enable(inst, app, client);
//			}
			//
			AppVO app = _commondao.find(new StringBuilder("select * from pb_bd_app where code=${code}"), JO.gen("code",app_code), AppVO.class, "没有找到"+app_code+"应用");
			//
			String cb_clazz = "service/app/active/"+app_code;
			MultiTenantManagementBusinessService mtService = null;
			try {
				mtService = appctx.getBean(cb_clazz, MultiTenantManagementBusinessService.class);//很有可能没拿到
				mtService.enable(inst, app, client);
			} catch (BeansException e) {
				//FIXME 没找到就算了,但是日志里得提一句，不然鬼知道是出错了，还是啥
			}
			//
			//建立起company-app的关联关系
			RltCompanyApp rlt_ca = new RltCompanyApp();
			rlt_ca.company_id = inst.id;
			rlt_ca.app_id = app.id;
			_commondao.batchReplace(Arrays.asList(rlt_ca));//FIXME 暂缺启用时间，结束时间等关键信息，这样没法付费
		} catch (Exception e) {
			processError(e);
		}
	}

	@Override
	public void disableApp(String company_code, String app_code, SecuritySession client) throws BusinessError {
		// TODO Auto-generated method stub

	}

}
