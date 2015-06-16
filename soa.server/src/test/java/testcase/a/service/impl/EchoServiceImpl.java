package testcase.a.service.impl;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import net.popbean.a.service.EchoService.Iface;
@Service("service/echo/mock")
public class EchoServiceImpl implements Iface {

	@Override
	public String execute(String name) throws TException {
		return "echo.execute:"+name;
	}

}
