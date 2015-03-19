package net.popbean.pf.bill.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.bill.helpers.BillModelHelper;
import net.popbean.pf.bill.service.BillModelBusinessService;
import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.exception.ErrorBuilder;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * bill model
 * @author to0ld
 *
 */
@Service("service/pf/bill/model/redis")
public class BillModelBusinessServiceRedisImpl extends AbstractBusinessService implements BillModelBusinessService {
	@Autowired
	@Qualifier("redisTemplate")
	protected RedisTemplate<String, BillModel> operations;
	public static final String COLL_OPLOG = "pb_op_log";
	/**
	 * 
	 * @param code
	 * @param stage
	 * @param hasData
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	@Override
	public BillModel find(String code, String stage, boolean hasData,SecuritySession client) throws BusinessError {
		//FIXME 是否要提供一个参数让外部来控制是否带默认值回去
		BillModel model = makeModel(code, stage);
		if(hasData){//解析默认值，可以再定义一个方法
			JSONObject data = makeDefValue(model, client);
			model.data = data;
		}
		return model;
	}
	/**
	 * 根据单据号和场景去找模型
	 * @param code
	 * @param stage
	 * @return
	 * @throws BusinessError
	 */
	@Cacheable(value="service/bill/model",key="code+'-'+stage")
	private BillModel makeModel(String code,String stage)throws BusinessError{
		BillModel model = null;
		if(!StringUtils.isBlank(stage)){
			model = operations.opsForValue().get("bill/"+code+"/"+stage);//bill/	
		}
		if(model !=null){
			return model;
		}
		//如果没有找到就找默认的返回
		model = operations.opsForValue().get("bill/"+code);//bill/
		return model;
	}
	private JSONObject makeDefValue(BillModel model,SecuritySession session){
		throw new UnsupportedOperationException("尚未实现");
	}
	/**
	 * 验证模型是否可用，主要是：是否完备
	 * @param model
	 * @return
	 * @throws BusinessError
	 */
	public boolean valid(BillModel model)throws BusinessError{
		throw new UnsupportedOperationException("暂未实现好吧");
	}
	/**
	 * 
	 */
	
	@Override
	public BillModel findById(String code, String id, boolean hasData, SecuritySession client) throws BusinessError {
		try {
			BillModel model = find(code, null, false, client);
			EntityModel main_model = BillModelHelper.convert(model.main);
			StringBuilder sql = new StringBuilder("select * from "+main_model.code+" where "+main_model.findPK()+"=${id}");
			
			JSONObject main_data = _commondao.find(sql, JO.gen("id",id));//从model中得到
			String stage = BillModelHelper.findStage(model, main_data);
			return find(code, stage, hasData, client);			
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	@Override
	public void save(BillModel model, SecuritySession session) throws BusinessError {
		if(model == null){
			ErrorBuilder.createSys().msg("模型为空,无法进行后续处理").execute();//FIXME 不明确空的原因，无法给出相应建议
		}
		if(StringUtils.isBlank(model.code)){
			ErrorBuilder.createSys().msg("单据模型编码为空,无法进行后续处理").execute();//FIXME 不明确空的原因，无法给出相应建议
		}
		String key = "bill/"+model.code;
		if(!StringUtils.isBlank(model.stage)){
			key+= "/"+model.stage;
		}
		operations.opsForValue().set(key, model);
	}
	@Override
	@CacheEvict(value="service/bill/model",key="code+'-'+stage")
	public void flush(String code, String stage,SecuritySession session) throws BusinessError {
		//FIXME 记录一下被谁调用的
	}
}
