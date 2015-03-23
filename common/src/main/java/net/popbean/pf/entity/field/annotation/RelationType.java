package net.popbean.pf.entity.field.annotation;

/**
 * 关系类型
 * 
 * @author to0ld
 *
 */
public enum RelationType {
	None, //无关联
	Master,//主表,source
	Slave//子表,target
}
