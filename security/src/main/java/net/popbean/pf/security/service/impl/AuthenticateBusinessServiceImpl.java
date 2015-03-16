package net.popbean.pf.security.service.impl;

import java.util.List;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.model.helper.EntityModelHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.security.helper.OpConst;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.service.CustomAuthenticationBusinessService;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.CompanyVO;
import net.popbean.pf.security.vo.OrgVO;
import net.popbean.pf.security.vo.RoleVO;
import net.popbean.pf.security.vo.SecuritySession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;


@Service("service/pf/security/auth")
public class AuthenticateBusinessServiceImpl extends AbstractBusinessService implements AuthenticationBusinessService {
	@Autowired
	private ApplicationContext appctx;
	//FIXME 眼下先写死，以后再作config service
	@Value("${authen.impl}")
	private String authen = "pf.security.authen.local";	


	@Override
	public SecuritySession auth(String account_code, String accountPwd) throws BusinessError {

		try {
			//根据配置信息选择authen的实现
			CustomAuthenticationBusinessService caService = appctx.getBean(authen,CustomAuthenticationBusinessService.class);
			caService.login(account_code, accountPwd);
			// 状态是否正常？
			 SecuritySession env = buildSession(account_code);
			//
			log(OpConst.AUTH_LOGIN,"登陆成功",env);
			return env;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

	public void logout(SecuritySession env) throws BusinessError {
		try {
			// 需要记录在案
			log(OpConst.AUTH_LOGOUT,"退出成功",env);
		} catch (Exception e) {
			processError(e);
		}
	}
	/**
	 * @deprecated 这个最好是放到将来的用户中心去
	 */
	@Cacheable(value="service/pf/security/auth",key="#account_code")
	@Override
	public SecuritySession buildSession(String account_code)throws BusinessError{//这需要很高的授权
		try {
			if (StringUtils.isBlank(account_code)) {
				ErrorBuilder.createSys().msg("用户名(" + account_code + ")错误").execute();
			}
			StringBuilder sql = new StringBuilder("select * from pb_bd_account where code=${account_code} ");
			AccountVO account_inst = _commondao.find(sql, JO.gen("account_code",account_code),AccountVO.class,"没有找到编码为"+account_code+"的数据");
			

			// FIXME 是否已经到了启用时间？是否已经超出有效期？
			// 状态是否正常？
			int istat = account_inst.stat;
			if(istat != AccountVO.STAT_NONE){
				String type = "未知";
				if (istat == AccountVO.STAT_BLOCK) {
					type = "黑名单";
				} else if (istat == AccountVO.STAT_DEL) {
					type = "删除";
				} else if (istat == AccountVO.STAT_SEAL) {
					type = "封存";
				} else if (istat == AccountVO.STAT_EDIT) {
					type = "编辑";
				}
				ErrorBuilder.createSys().msg("当前用户(" + account_inst.code + ")的状态为[" + type + "],不能登录，请与运维人员联系").execute();
			}

			// 只查询这个用户具有的正常角色
			JSONObject role_inst = JO.gen(
				"PK_ACCOUNT", account_inst.id,
				"ISTAT", AccountVO.STAT_NONE,
				"COMPANY_CRT_REF", account_inst.company_crt_ref
			);
			//获得该用户关联的有效角色
			sql = new StringBuilder("select a.* from rlt_role_account a left join pb_bd_role b on (a.pk_subject=b.pk_role) where a.pk_resource=${PK_ACCOUNT} and b.istat=${ISTAT}");
			List<RoleVO> roleList = _commondao.query(sql, role_inst,RoleVO.class);
			//
			sql = new StringBuilder(" select a.* from pb_bd_company a left join pb_bd_account b on (concat(a.id,'"+EntityModelHelper.REF_SPLIT+"',a.name)=b.company_crt_ref) where 1=1 and b.id=${PK_ACCOUNT}");
			CompanyVO company = _commondao.find(sql, JO.gen(), CompanyVO.class, null);

			//
			
			StringBuilder sql1 = new StringBuilder(" select b.PK_DEPT,b.DEPT_CODE,b.DEPT_NAME,b.MEMO,b.CRT_TS,b.ISTAT,b.SERIESCODE ");
			sql1.append(" from RLT_ACCOUNT_DEPT a left join PB_BD_ORG b on ( a.PK_DEPT = b.PK_DEPT ) ");
			sql1.append(" where a.PK_ACCOUNT = ${PK_ACCOUNT} and b.ISTAT= 3 order by itype ");
			OrgVO org = _commondao.find(sql1, JO.gen("PK_ACCOUNT",account_inst.id),OrgVO.class,null);
			
			//
			SecuritySession env = new SecuritySession();
			env.account_code = account_inst.code;
			env.account_name = account_inst.name;
			env.account_id = account_inst.id;
			env.company = company;
			env.roles = roleList;
			env.org = org;
			return env;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
}
