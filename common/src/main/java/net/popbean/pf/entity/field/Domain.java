package net.popbean.pf.entity.field;




/**
 * 参考RoundingMode的实现方式
 * @author to0ld
 *
 */
public enum Domain{
	pk(60), code(60), date(5), timestamp(7), memo(1000), money(12),seriescode(320),stat(19),clob(23),integer(29),ref(60);
	int _value;
	private Domain(int value){
		this._value = value;
	}
	public static Domain valueOf(int rm){
		return Domain.pk;
	}
	public int getLength(){
		return _value;
	}
}
