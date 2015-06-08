package net.popbean.pf.zk;

/**
 * 自定义异常
 */
public class ThriftException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4615337184141652952L;

	public ThriftException() {
		super();
	}

	public ThriftException(String msg) {
		super(msg);
	}

	public ThriftException(Throwable e) {
		super(e);
	}

	public ThriftException(String msg, Throwable e) {
		super(msg, e);
	}
}
