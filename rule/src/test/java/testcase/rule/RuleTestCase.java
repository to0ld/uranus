package testcase.rule;

import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.dataset.vo.EnumRangeVO;
import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.VOHelper;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.rule.service.RuleBusinessService;
import net.popbean.pf.rule.vo.RuleDetail;
import net.popbean.pf.rule.vo.RuleModel;
import net.popbean.pf.rule.vo.RuleParam;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

@ContextConfiguration(locations = { "classpath:/spring/app.test.xml" })
public class RuleTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/rule")
	RuleBusinessService ruleService;
	//
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	@Test
	public void simpleRule(){
		try {
			SecuritySession session = mockLogin();
			//建表
			esService.syncDbStruct(RuleModel.class, session);
			esService.syncDbStruct(RuleDetail.class, session);
			esService.syncDbStruct(RuleParam.class, session);
			esService.syncDbStruct(EnumRangeVO.class, session);
			//清理数据
			StringBuilder sql = new StringBuilder("delete from pb_pf_rule");
			commonService.executeChange(sql, session);
			sql = new StringBuilder("delete from pb_pf_rule_detail");
			commonService.executeChange(sql, session);
			//写入规则
			RuleModel model = new RuleModel();
			model.code = "rule_test";
			model.name = "测试规则";
			model.memo = "只是简单的用于测试，而已";
			
			//  ['key1'] > 100 & ['key2'] == 'hunan' then return://istat=1
			// ['key2']
			//
			List<IValueObject> list = new ArrayList<>();
			String rule_id = commonService.save(model, null);
			RuleDetail detail = new RuleDetail();
			detail.cond_exp = "['key1'] > 100 && ['key2'] == 'beijing'";
			detail.exec_exp = "return://istat=1";
			detail.rule_id = rule_id;
			list.add(detail);
			detail = new RuleDetail();
			detail.cond_exp = "['key1'] <= 100 ";
			detail.exec_exp = "return://istat=0";
			detail.rule_id = rule_id;
			list.add(detail);
			commonService.batchInsert(list, null);
			//拼凑模拟数据
			JSONObject context = JO.gen("key1",150,"key2","beijing");
			
			//评估
			JSONObject result = ruleService.eval("rule_test", context, session);
			Assert.assertNotNull(result);
			Assert.assertEquals(result.getBooleanValue("condition"), true);//判断条件有没有被诊断到
			Assert.assertEquals(result.getString("data"), "istat=1");
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
