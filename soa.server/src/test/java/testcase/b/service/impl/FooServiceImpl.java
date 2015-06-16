package testcase.b.service.impl;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import net.popbean.b.service.FooService.Iface;
@Service("service/foo/mock")
public class FooServiceImpl implements Iface {

	@Override
	public String bar(String name) throws TException {
		return "foo.bar:"+name;
	}

}
