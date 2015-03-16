package testcase.pf.dataset.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.dataset.service.CustomDataSetBusinessService;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.persistence.helper.DaoConst.Paging;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * 只是用于测试
 * @author to0ld
 *
 */
@Service("service/test/dataset/wokao")
public class WoKaoCustomDataSetBusinessService extends AbstractBusinessService implements CustomDataSetBusinessService {

	@Override
	public List<JSONObject> fetch(DataSetModel model, JSONObject param, Paging paging, SecuritySession client) throws BusinessError {
		List<JSONObject> ret = new ArrayList<>();
		ret.add(JO.gen("code","code_value_1","key1","key1_value_1","key2","key2_value_2"));
		ret.add(JO.gen("code","code_value_2","key1","key1_value_1","key2","key2_value_2"));
		return ret;
	}

}
