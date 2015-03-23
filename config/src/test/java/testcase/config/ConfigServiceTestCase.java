package testcase.config;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.config.service.ConfigBusinessService;
import net.popbean.pf.config.vo.ConfigFieldModel;
import net.popbean.pf.config.vo.ConfigValue;
import net.popbean.pf.config.vo.ConfigModel;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.helper.IOHelper;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import testcase.config.vo.MailConfig;


@ContextConfiguration(locations={"classpath:/spring/app.test.xml"})
public class ConfigServiceTestCase extends AbstractTestNGSpringContextTests{
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/config/mysql")
	ConfigBusinessService configService;
	//
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	/**
	 * 
	 */
	@Test
	public void single_key(){
		try {
			SecuritySession session = mockLogin();
			//假定一个是否发送邮件的选项的控制
			//0-模拟数据
			String content = IOHelper.readStringFromFileByChar("classpath:data/config/single_key.data", "utf-8");
			JSONObject mock = JSON.parseObject(content);
			ConfigModel model = mock.getObject("data", ConfigModel.class);
			
			//1-建表
			esService.syncDbStruct(ConfigModel.class,session);
			esService.syncDbStruct(ConfigFieldModel.class,session);
			esService.syncDbStruct(ConfigValue.class, session);
			//2-清理数据
			StringBuilder sql = new StringBuilder("delete from pb_pf_config where code=${code}");//id=app_code:code
			commonService.executeChange(sql, JO.gen("code",model.code), session);
			sql = new StringBuilder("delete from pb_pf_config_field where config_id=${id}");
			sql = new StringBuilder(" delete from pb_pf_config_value where config_id=${id} ");
			//3-保存参数模型(以后可以用单据解决?)
//			inst.id = IdGenHelper.genID("testcase", inst.code);
			configService.saveModel(model,session);//自动构建参数
			//4-尝试设定参数
			MailConfig value = new MailConfig();
			value.error_send_mail = false;
			configService.changeValue("testcase", model.code, value, session);
			//所有的数据都存一个表中，所以，可以制造一个假象：configService.changeValue("testcase",SomePojo,session) vs changeValue(SomePojo,session)
			//可以搞出一个基类来
			//5-取数
			MailConfig ret = (MailConfig)configService.findValue("testcase", model.code, session);//不知道具体的实现是啥啊。。。
			Assert.assertNotNull(ret);
			Assert.assertEquals(false, ret.error_send_mail);
			//performance
			int LOOP = 1000;
			long start = System.currentTimeMillis();
			for(int i=0;i<LOOP;i++){
				configService.findValue("testcase", model.code, session);//不知道具体的实现是啥啊。。。
			}
			long end = System.currentTimeMillis();
			System.out.println("loop=x;total="+(end-start));
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
	/**
	 * 
	 */
	@Test
	public void multi_key(){//比如数据源
		
	}
}
