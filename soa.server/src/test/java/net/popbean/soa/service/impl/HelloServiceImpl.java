package net.popbean.soa.service.impl;

import org.apache.thrift.TException;

import net.popbean.soa.service.HelloService.Iface;

public class HelloServiceImpl implements Iface {

	public String say(String name) throws TException {
		return "hello "+name;
	}

}
