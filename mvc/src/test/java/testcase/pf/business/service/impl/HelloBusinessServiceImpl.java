package testcase.pf.business.service.impl;

import org.springframework.stereotype.Service;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import testcase.pf.business.service.HelloBusinessService;
/**
 * 
 * @author to0ld
 *
 */
@Service("service/testcase/hello")
public class HelloBusinessServiceImpl extends AbstractBusinessService implements HelloBusinessService {

	@Override
	public String say(String name) {
		return "hello "+name;
	}

}
