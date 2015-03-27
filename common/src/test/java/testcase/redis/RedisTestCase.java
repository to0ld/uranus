package testcase.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.popbean.pf.security.vo.AccountVO;
import net.popbean.pf.testcase.TestHelper;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;

@ContextConfiguration(locations = { "classpath:/spring/app.test.xml" })
public class RedisTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("redisTemplate")
	protected RedisTemplate<String, JSONObject> op_jo;
	@Autowired
	@Qualifier("redisTemplate")
	protected RedisTemplate<String, AccountVO> op_pojo;
	@Test
	public void simple_query(){//验证查询的处理：不能像document一样处理
		try {
			//模拟插入100条记录，分别写入value和list，然后进行查询
			List<AccountVO> list = buildPojo(100);
			AccountVO[] vo_list = list.toArray(new AccountVO[0]);
			
			op_pojo.opsForList().rightPushAll("test_query", vo_list);
			//验证是否插入
			long size = op_pojo.opsForList().size("test_query");
			Assert.assertTrue(size!=0);
			//
			AccountVO p = new AccountVO();
			p.code = "code_57";
			SortQuery<String> query = SortQueryBuilder.sort("test_query").noSort().get("#").build();
			List<AccountVO> tmp = op_pojo.sort(query);
			Assert.assertNotNull(tmp);
			//
//			op_pojo.delete("test_query");
			//
//			List<JSONObject> jo_list = buildJO(100);
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	/**
	 * 
	 * @param loop
	 * @return
	 */
	private List<JSONObject> buildJO(int loop){
		List<JSONObject> ret = new ArrayList<>();
		for(int i=0;i<loop;i++){
			JSONObject jo = new JSONObject();
			jo.put("code", "code_"+i);
			jo.put("name","name_"+i);
			ret.add(jo);
		}
		return ret;
	}
	private List<AccountVO> buildPojo(int loop){
		List<AccountVO> ret = new ArrayList<>();
		for(int i=0;i<loop;i++){
			AccountVO inst = new AccountVO();
			inst.code = "code_"+i;
			inst.name = "name_"+i;
			ret.add(inst);
		}
		return ret;
	}
}
