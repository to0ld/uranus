package testcase.pf.dataset;

import net.popbean.pf.business.service.CommonBusinessService;
import net.popbean.pf.dataset.service.DataSetBusinessService;
import net.popbean.pf.dataset.vo.DataSetFieldModel;
import net.popbean.pf.dataset.vo.DataSetModel;
import net.popbean.pf.dataset.vo.SourceType;
import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.testcase.TestHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * -[?] 模型管理(cache，最好对有无都要进行对比) -[] 各种数据源的数据集取数验证(cache) -[?]
 * 模型简化:tpl？没有必要再搞个列表吧
 * 
 * @author to0ld
 *
 */
@ContextConfiguration(locations = { "classpath:/spring/app.test.xml" })
public class DatasetServiceTestCase extends AbstractTestNGSpringContextTests {
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	@Autowired
	@Qualifier("service/pf/common")
	CommonBusinessService commonService;
	@Autowired
	@Qualifier("service/pf/dataset")
	DataSetBusinessService dsService;
	//
	private SecuritySession mockLogin(){
		SecuritySession session = new SecuritySession();
		return session;
	}
	//
	@Test
	public void sql() {// 测试来自于sql的数据集
		try {
			SecuritySession session = mockLogin();
			// 建表
			esService.syncDbStruct(CustmerVO.class, session);
			// 清理表
			StringBuilder sql = new StringBuilder(" delete from pb_test_custmer ");
			commonService.executeChange(sql, session);
			//构建模拟数据
			// 插入数据
			
			// 保存dataset model
			// 读取模型
			// 读取数据
			// 验证
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}

	@Test
	public void spring() {//custom dataset service
		try {
			//建表(dataset+field)
			SecuritySession session = mockLogin();
			//FIXME 需要加param么？
			esService.syncDbStruct(DataSetModel.class, session);
			esService.syncDbStruct(DataSetFieldModel.class, session);
			//构建模拟的dataset model
			DataSetModel model = new DataSetModel();
			model.app_code = "testcase";
			model.code = "custom_mock";
			model.name = "自定义-模拟";
			model.exec_exp = "service/test/dataset/wokao";
			model.src_type = SourceType.spring;//FIXME 应该制定策略，entity不能使用enum
			//保存模型
			String pk_ds = commonService.save(model, null);
			//
			//获取模型及数据
			DataSetModel ret = dsService.findModel(pk_ds, JO.gen(), true, false, null, session);
			Assert.assertNotNull(ret);
			Assert.assertNotNull(ret.data);
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}

	@Test
	public void groovy() {

	}

	@Test
	public void restful() {

	}

	@Test
	public void defdoc() {

	}

	@Entity(code = "pb_test_custmer")
	private static class CustmerVO implements IValueObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3340290832096001736L;
		@Field(domain = Domain.pk)
		public String custmer_id;
		@Field
		public String custmer_code;
		@Field
		public String custmer_name;
		@Field(domain = Domain.memo)
		public String custmer_memo;
	}
}
