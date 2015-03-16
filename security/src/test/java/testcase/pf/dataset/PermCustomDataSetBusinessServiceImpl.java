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
/**
 * 用于测试，模拟功能权限
 * @author to0ld
 *
 */
@Service("service/pf/dataset/spring_perm_test")
public class PermCustomDataSetBusinessServiceImpl extends AbstractBusinessService implements CustomDataSetBusinessService {

	@Override
	public List<JSONObject> fetch(DataSetModel model, JSONObject param, Paging paging, SecuritySession client) throws BusinessError {
		List<JSONObject> ret = new ArrayList<>();
		JSONObject inst = JO.gen("id","perm:testcase:node_a","code","node_a");
		ret.add(inst);
		inst = JO.gen("id","perm:testcase:node_b","code","node_b");
		ret.add(inst);
		return ret;
	}

}
