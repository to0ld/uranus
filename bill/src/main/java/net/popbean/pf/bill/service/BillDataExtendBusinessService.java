package net.popbean.pf.bill.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;
/**
 * 用于扩展标准之外的单据行为
 * @author to0ld
 *
 */
public interface BillDataExtendBusinessService {
	/**
	 * 用于在保存前使用
	 * @param model
	 * @param data
	 * @param env
	 * @throws Exception
	 */
	public void validForSave(BillModel model, JSONObject data,Boolean isAdd, SecuritySession env)throws Exception;
	/**
	 * 保存前处理
	 * @param model
	 * @param data
	 * @param env
	 * @throws Exception
	 */
	public JSONObject beforeSave(BillModel model, JSONObject data,Boolean isAdd, SecuritySession env)throws Exception;
	/**
	 * 保存后处理
	 * @param model
	 * @param data
	 * @param env
	 * @throws Exception
	 */
	public void afterSave(BillModel model, JSONObject data,Boolean isAdd, SecuritySession env)throws Exception;
	/**
	 * 
	 * @param model
	 * @param data
	 * @param env
	 * @throws Exception
	 */
	public void validForDelete(BillModel model, JSONObject data, SecuritySession env)throws Exception;
	/**
	 * 删除单据数据前校验 
	 * @param model
	 * @param billdata
	 * @param env
	 * @throws Exception
	 */
	public void beforeDelete(BillModel model, JSONObject inst, SecuritySession env)throws Exception;
	/**
	 * 删除之后校验
	 * @param model
	 * @param data
	 * @param env
	 * @throws Exception
	 */
	public void afterDelete(BillModel model, JSONObject data, SecuritySession env)throws Exception;
	/**
	 * 自定义子集查询
	 * @param model
	 * @param pk
	 * @param slave_entity_code
	 * @param client
	 * @return
	 * @throws Exception
	 */
	public List<JSONObject> fetchSlave(BillModel model,String pk,String slave_entity_code,SecuritySession client)throws Exception;
	/**
	 * 
	 * @param model
	 * @param condition
	 * @param needData
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	public List<JSONObject> fetchMain(BillModel model, JSONObject condition, boolean needData, SecuritySession client) throws BusinessError;
	/**
	 * 是否启用自定义查询
	 * 
	 * @return
	 * @throws BuzException
	 */
	public boolean enableCustomFetch() throws BusinessError;

	public boolean enableCustomFind() throws BusinessError;

	public boolean enableSlaveDataCustomFetch(String slave_code) throws BusinessError;

	public JSONObject findBillData(BillModel model, String data_id, Boolean includeSlave, SecuritySession client) throws BusinessError;
}
