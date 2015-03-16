package testcase.asm.method;

import org.objectweb.asm.util.ASMifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import testcase.vo.AccountVOWrapperTpl;

public class GenClassTestCase {
	@Test
	public void printClass(){
		try {
			ASMifier.main(new String[]{AccountVOWrapperTpl.class.getName()}); 
		} catch (Exception e) {
			Assert.fail("asmifier", e);
		}
	}
	public static class MyClassLoader extends ClassLoader {
		public Class<?> defineClass(String className, byte[] byteCodes) {
			return super.defineClass(className, byteCodes, 0, byteCodes.length);
		}
	}
}
