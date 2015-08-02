package net.popbean.pf.entity.struct.impl;

import org.apache.commons.lang3.StringUtils;

import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.struct.EntityStruct;

public class MysqlEntityStructImpl implements EntityStruct {

	@Override
	public String create(EntityModel model)throws Exception {
		String tableCode = model.code;
		String ret = " create table " + tableCode + "(";
		//
		int pos = 0;
		for (FieldModel field : model.field_list) {
			if (pos != 0) {
				ret += ",";
			}
			ret += convertField(field) + "\n";
			pos++;
			if(Domain.ref.equals(field.type)){
				
			}
		}
		// 处理主键
		FieldModel pk = model.findPK();
		if (pk != null && (Domain.pk.equals(pk.type))) {// 如果是临时表，无主键，那就用reffield吧
			ret += " ,constraint PK_" + tableCode + " primary key (" + pk.code + ") ";
		}
		ret += ")";
		return ret;
	}

	/**
	 * @param newField entitymodel中的结构
	 * @param oldField 当前数据库中的结构
	 */
	@Override
	public String alter(String table_code, FieldModel newField, FieldModel oldField) throws Exception {
		String ret = "";
		if (newField == null && oldField == null) {// 都是空，就不玩了
			return null;
		}
		if (newField == null) {// 为drop的场景
			ret += "ALTER TABLE " + table_code + " DROP " + oldField.code + "";
			return ret;
		}
		if (oldField == null) {// add的场景
			ret += "ALTER TABLE " + table_code + " ADD (" + convertField(newField) + ")";
			return ret;
		}
		if (!newField.code.toLowerCase().equals(oldField.code.toLowerCase())) {// 这个估计很难被执行到,因为新老之间没有对应关系
			ret += "ALTER TABLE " + table_code + " CHANGE " + oldField.code + " " + convertField(newField);
			return ret;
		}
		return null;
	}
	@Override
	public String convertField(FieldModel field) throws Exception {
		String ret = field.code + " ";
		if (Domain.code.equals(field.type) || Domain.memo.equals(field.type) || Domain.seriescode.equals(field.type)) {
			ret += " varchar(" + field.length + ")";
//			ret += " varchar(" + Domain.Code.getLength() + ")";
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default '" + field.def_value + "'";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		} else if (Domain.pk.equals(field.type)) {
//			ret += " varchar(" + field.length + ")";
			ret += " varchar(" + Domain.pk.getLength() + ")";
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default '" + field.def_value + "'";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		}else if(Domain.ref.equals(field.type)){
			ret += " varchar(" + Domain.ref.getLength() + ")";
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default '" + field.def_value + "'";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		} else if (Domain.date.equals(field.type)) {
			ret += " date ";
			if (field.isRequired()) {
				ret += " not null ";
			}
		} else if (Domain.money.equals(field.type)) {
			ret += " DECIMAL(" + Domain.money.getLength() + "," + field.fidelity + ")";
			
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default " + field.def_value + "";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		} else if (Domain.stat.equals(field.type)) {
			ret += " tinyint ";
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default " + field.def_value + "";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		} else if (Domain.timestamp.equals(field.type)) {
			ret += " TIMESTAMP ";
			if (field.isRequired()) {
				ret += " not null ";
			}else{
				ret += "  null ";
			}
			if (field.def_value != null) {//FIXME 强行给人无视了？
//				ret += " deault 0 ";//暂时不支持对timestamp设置初始值
			} else {
				if(field.code.equalsIgnoreCase("LM_TS")){//当乐观锁用的好吧
					ret += " default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ";
				}
			}
			
		} else if (Domain.integer.equals(field.type)) {
			ret += " int ";
			if (!StringUtils.isBlank(field.def_value)) {
				ret += " default " + field.def_value + "";// 需要考虑的是，如果字符还得单引号
			}
			if (field.isRequired()) {
				ret += " not null ";
			}
		}
		return ret;
	}
}
