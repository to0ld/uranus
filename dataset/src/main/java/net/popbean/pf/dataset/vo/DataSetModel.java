package net.popbean.pf.dataset.vo;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.field.annotation.Field;
import net.popbean.pf.entity.impl.AbstractValueObject;
/**
 * 数据集的数据模型
 * @author to0ld
 *
 */
@Entity(code="pb_pf_ds")
public class DataSetModel extends AbstractValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5816415774814995076L;
	//
	@Field(domain=Domain.code,name="编码")
	public String code;
	@Field(domain=Domain.code,name="名称")
	public String name;
	@Field(domain=Domain.code,name="所属应用")
	public String app_code;//所属应用(如果要加授权,app_key+secret)
	@Field(name="数据源类型")
	public SourceType src_type;
	@Field(name="取数规则")
	public String rule_exp = "data";//比如从{data:[{}...{}]}中取数据，就可以写data
	@Field(domain=Domain.memo)
	public String exec_exp;//规则
	@Field(domain=Domain.code,name="显示表达式")
	public String show_exp;//显示的内容；采用spel
	@Field(domain=Domain.memo)
	public String memo;//备注
	//
	public List<DataSetFieldModel> field_list;
	//
	public String pk_field;//为了增强虚设不需要存数据库中
	public String show_field;//用于显示
	public List<JSONObject> data ;
	
}
