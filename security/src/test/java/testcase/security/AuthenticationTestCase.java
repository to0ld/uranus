package testcase.security;

import net.popbean.pf.testcase.TestHelper;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 鉴权
 * -[] account_id有无指定perm_id/uri的权限
 * -[?] 数据权限(资源授权来做)
 * -[?] 切换认证登录身份
 * -
 * @author to0ld
 */
public class AuthenticationTestCase {
	@Test
	public void simple(){//FIXME 统一采用springmvc的测试方式，直接构建http request进行测试
		try {
			//FIXME 模拟登陆：采用http client的方式
			
			//FIXME 创建用户
			//FIXME 创建角色
			//FIXME 创建权限对象(虚拟一个test的app：以一个app的安装管理为例子好了
			//FIXME 映射：角色-用户
			//FIXME 授权：角色-权限
			//FIXME 鉴权
			//FIXME 来自页面端的鉴权：掐掉访问头进行识别,对授权信息进行缓存，识别部分简化成map查找
			//FIXME 开发鉴权的拦截器(根据http header中的ref来判断，假定api的调用必须是有ref的)
			//FIXME 场景1：打开一个指定的url(从界面中点击)
			//FIXME 场景2：拷贝一个url，可能有多余的参数，到浏览器中打开(必须要约定perm url是restful的无?，截取也是这么截取)
		} catch (Exception e) {
			Assert.fail(TestHelper.getErrorMsg(e), e);
		}
	}
}
