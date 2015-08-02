package net.popbean.pf.security.service.impl;

import java.util.List;

import org.apache.commons.codec.digest.Md5Crypt;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.service.AccountBusinessService;
import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.security.vo.SecuritySession;

public class AccountBusinessServiceImpl extends AbstractBusinessService implements AccountBusinessService {

	@Override
	public Integer changeStat(List<String> pk_account_list, Integer newStat) throws BusinessError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean validateAccountCode(String account_code) throws BusinessError {

		return null;
	}

	@Override
	public String signUp(AccountVO account, SecuritySession client) throws BusinessError {
		try {
			//验证；锁定
			StringBuilder sql = new StringBuilder("select * from pb_bd_account where code=${code} $[and id!=${id}]");
			_commondao.assertTrue(sql, JO.gen("code",account.code,"id",account.id), account.code+"的用户已经存在");
			//加盐
			String salt_str = Long.toString(System.currentTimeMillis());//用时间戳来加盐
			account.salt = salt_str;
			account.pwd = Md5Crypt.apr1Crypt("13707460987", salt_str);
			//FIXME lock
			_commondao.save(account,false,account.code);
			return account.code;
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}

}
