package net.popbean.pf.soa.server.helper.gen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ThriftStruct implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5111338045537240414L;
	/**
	 * 
	 */
	public String name;
	public List<ThriftField> fields = new ArrayList<ThriftField>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThriftStruct other = (ThriftStruct) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
