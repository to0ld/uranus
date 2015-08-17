package net.popbean.pf.security.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.mvc.bind.support.JsonObjectArgumentResolver;
import net.popbean.pf.mvc.controller.BaseController;
import net.popbean.pf.security.service.AuthenticationBusinessService;
import net.popbean.pf.security.vo.PermVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
/**
 * 
 * @author to0ld
 *
 */

@Controller
public class AuthorityController extends BaseController{
//	@Autowired
//	@Qualifier("service/pf/security/auth")
//	AuthenticationBusinessService authService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	//
	@ResponseBody
	@RequestMapping("service/{appcode}/nodes")
	public List<JSONObject> fetchNodesByAccount(@PathVariable("appcode")String app_code,@RequestParam(value="account_code",defaultValue="admin")String account_code)throws Exception{
		//FIXME 应该是调用resource-mapping中的实现(需要带参数)
		// select a.* from pb_bd_perm a left join rlt_role_perm b on (a.id = b.perm_id) where b.role_id in (select c.id from rlt_role_account c inner join pb_bd_account d on (c.account_id=d.id) where d.code=${code})
		StringBuilder sql = new StringBuilder(" select a.* from pb_bd_perm a  ");
		sql.append(" left join rlt_role_perm b on (a.id = b.perm_id) ");
		sql.append(" where b.role_id in (select c.role_id from rlt_role_account c inner join pb_bd_account d on (c.account_id=d.id) where d.code=${code} ) ");
		sql.append(" and a.app_code=${app_code} ");
		sql.append(" order by a.type,a.serial ");
		List<JSONObject> ret =  commonService.query(sql, JO.gen("app_code",app_code,"code",account_code), null);
		ret = build(ret);
		return ret;
	}
	private List<JSONObject> build(List<JSONObject> original){
		List<JSONObject> ret = new ArrayList<>();
		//第一轮找到folder，顺便搞定node
		Map<JSONObject,List<JSONObject>> bus = new HashMap<>();
		for(JSONObject jo : original){
			if(JOHelper.equalsStringValue(jo, "type", 3)){//folder
				boolean has_folder = false;
				Iterator<JSONObject> keys = bus.keySet().iterator();
				while(keys.hasNext()){
					JSONObject k = keys.next();
					if(k.getString("code").equals(jo.getString("code"))){
						has_folder = true;
						break;
					}
				}
				if(!has_folder){
					bus.put(jo, new ArrayList<JSONObject>());
				}
			}else{//node的情况
				String folder_id = jo.getString("folder_id");
				Iterator<JSONObject> keys = bus.keySet().iterator();
				while(keys.hasNext()){
					JSONObject k = keys.next();
					if(k.getString("id").equals(folder_id)){
						bus.get(k).add(jo);
						break;
					}
				}
			}
		}
		Iterator<JSONObject> keys = bus.keySet().iterator();
		while(keys.hasNext()){
			JSONObject k = keys.next();
			k.put("children", bus.get(k));
			ret.add(k);
		}
		return ret;
	}
}
