package testcase.pf.dataset;

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
@Service("service/pf/dataset/spring_role_test")
public class RoleCustomDataSetBusinessServiceImpl extends AbstractBusinessService implements CustomDataSetBusinessService {

	@Override
	public List<JSONObject> fetch(DataSetModel model, JSONObject param, Paging paging, SecuritySession client) throws BusinessError {
		List<JSONObject> ret = new ArrayList<>();
		JSONObject inst = JO.gen("id","testcase:admin","code","admin");
		ret.add(inst);
		inst = JO.gen("id","testcase:emp","code","emp");
		ret.add(inst);
		return ret;
	}

}
