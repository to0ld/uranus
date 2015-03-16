package testcase.vo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import net.popbean.pf.entity.IValueObjectWrapper;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.helper.VOHelper;

public class AccountVOWrapper implements IValueObjectWrapper<AccountVO> {
	public void set(AccountVO paramAccountVO, String paramString, Object paramObject) {
		if ("pk_account".equals(paramString)) {
			paramAccountVO.pk_account = ((String) VOHelper.cast(String.class, paramObject));
			return;
		}
		if ("code_account".equals(paramString)) {
			paramAccountVO.code_account = ((String) VOHelper.cast(String.class, paramObject));
			return;
		}
		if ("money_account".equals(paramString)) {
			paramAccountVO.money_account = ((BigDecimal) VOHelper.cast(BigDecimal.class, paramObject));
			return;
		}
		if ("date_birth".equals(paramString)) {
			paramAccountVO.date_birth = ((Date) VOHelper.cast(Date.class, paramObject));
			return;
		}
		if ("ts_crt".equals(paramString)) {
			paramAccountVO.ts_crt = ((Timestamp) VOHelper.cast(Timestamp.class, paramObject));
			return;
		}
		if ("i_stat".equals(paramString)) {
			paramAccountVO.i_stat = ((Integer) VOHelper.cast(Integer.class, paramObject));
			return;
		}
		if ("memo_account".equals(paramString)) {
			paramAccountVO.memo_account = ((String) VOHelper.cast(String.class, paramObject));
			return;
		}
		if ("domain".equals(paramString)) {
			paramAccountVO.domain = ((Domain) VOHelper.cast(Domain.class, paramObject));
			return;
		}
		if ("domainx".equals(paramString)) {
			paramAccountVO.domainx = ((String) VOHelper.cast(String.class, paramObject));
			return;
		}
	}

	public Object get(AccountVO paramAccountVO, String paramString) {
		if ("pk_account".equals(paramString))
			return paramAccountVO.pk_account;
		if ("code_account".equals(paramString))
			return paramAccountVO.code_account;
		if ("money_account".equals(paramString))
			return paramAccountVO.money_account;
		if ("date_birth".equals(paramString))
			return paramAccountVO.date_birth;
		if ("ts_crt".equals(paramString))
			return paramAccountVO.ts_crt;
		if ("i_stat".equals(paramString))
			return paramAccountVO.i_stat;
		if ("memo_account".equals(paramString))
			return paramAccountVO.memo_account;
		if ("domain".equals(paramString))
			return paramAccountVO.domain;
		if ("domainx".equals(paramString))
			return paramAccountVO.domainx;
		return null;
	}
}