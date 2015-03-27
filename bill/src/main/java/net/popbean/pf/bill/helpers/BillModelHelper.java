package net.popbean.pf.bill.helpers;

import java.util.List;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.bill.vo.BillEntityModel;
import net.popbean.pf.bill.vo.BillEventModel;
import net.popbean.pf.bill.vo.BillFieldModel;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.entity.field.Domain;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.FieldModel;
import net.popbean.pf.entity.model.RelationModel;

public class BillModelHelper {
	public static BillFieldModel findPK(List<BillFieldModel> field_list){
		if(CollectionUtils.isEmpty(field_list)){
			return null;
		}
		for(BillFieldModel fm:field_list){
			if(Domain.Pk.equals(fm.domain)){
				return fm;
			}
		}
		return null;
	}
	/**
	 * 根据model以及主表数据得到stage
	 * @param model
	 * @param main_data
	 * @return
	 */
	public static String findStage(BillModel model,JSONObject main_data){
		//
		StandardEvaluationContext eval_ctx = new StandardEvaluationContext(main_data);
		ExpressionParser parser = new SpelExpressionParser();
		//
		for(BillEventModel event:model.events){
			if(event.exec_exp.startsWith("stage://")){//首先得是
				Expression exp_inst = parser.parseExpression(event.condition);
				boolean flag = exp_inst.getValue(eval_ctx,Boolean.class);
				if(flag){
					String ret = event.exec_exp.replaceAll("stage://", "");
					return ret;
				}
			}
			
		}
		return null;
	}
//	public static EntityModel convert(BillEntityModel model){
//		EntityModel ret = new EntityModel();
//		ret.code = model.code;
//		ret.name = model.name;
//		for(BillFieldModel bfm:model.fields){
//			FieldModel fm = convert(bfm);
//			ret.field_list.add(fm);
//		}
//		return ret;
//	}
	public static EntityModel convert(BillEntityModel model,RelationModel rm){
		EntityModel ret = new EntityModel();
		ret.code = model.code;
		ret.name = model.name;
		for(BillFieldModel bfm:model.fields){
			FieldModel fm = convert(bfm);
			ret.field_list.add(fm);
		}
		//如果是桥接，就放弃
		if(rm!=null && !RelationModel.TYPE_BRIDGE.equals(rm.type)){
			FieldModel fm = new FieldModel();
			fm.code = rm.id_key_slave;
			fm.type = Domain.Ref;
			ret.field_list.add(fm);
		}
		return ret;
	}
	private static FieldModel convert(BillFieldModel model){//并非完全转化，只是提供了基本元素
		FieldModel ret = new FieldModel();
		ret.code = model.code;
		ret.name = model.name;
		ret.type = model.domain;
		ret.length = model.domain.getLength();
		return ret;
	}
}
