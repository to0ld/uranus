package net.popbean.pf.security.service.impl;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.service.CustomAuthenticationBusinessService;
import net.popbean.pf.security.vo.AccountVO;

import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.stereotype.Service;
@Service("service/pf/security/authen/local")
public class LocalAuthenBusinessServiceImpl extends AbstractBusinessService implements CustomAuthenticationBusinessService {

	@Override
	public void login(String acc_code, String acc_pwd) throws BusinessError {
		try {
			StringBuilder sql = new StringBuilder("select pwd,salt from pb_bd_account where code=${code}");;
			AccountVO inst = _commondao.find(sql, JO.gen("code",acc_code), AccountVO.class, "没有找到编码为"+acc_code+"的用户");
			if(!inst.pwd.equals(Md5Crypt.apr1Crypt(acc_pwd, inst.salt))){
				throw new Exception("密码不正确");//FIXME 应该发出一个mq的消息
			}
		} catch (Exception e) {
			processError(e);
		}
	}

}
