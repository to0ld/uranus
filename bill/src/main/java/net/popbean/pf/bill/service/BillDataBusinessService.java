package net.popbean.pf.bill.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * - [] 基于元数据 - [] 提供CRUD
 * 
 * @author to0ld
 *
 */
public interface BillDataBusinessService {
	/**
	 * insert|update
	 * 
	 * @param code
	 * @param stage
	 * @param forceValid
	 *            是否强制校验
	 * @param data
	 * @param session
	 * @return
	 * @throws BusinessError
	 */
	public String save(String code, String stage, boolean forceValid, JSONObject data, SecuritySession session) throws BusinessError;

	/**
	 * 批量删除一组数据(实际是更新到status=-5) 先根据code和pk得到原始数据，然后根据
	 * 
	 * @param code
	 * @param pk_list
	 * @param env
	 * @throws BusinessError
	 */
	public void delete(String code, List<String> pk_list, SecuritySession env) throws BusinessError;
	/**
	 * 查询指定的
	 * @param code
	 * @param stage
	 * @param pk_value_main
	 * @param slave_entity_code
	 * @param session
	 * @return
	 * @throws BusinessError
	 */
	List<JSONObject> fetchSlaveData(String code, String stage, String pk_value_main, String slave_entity_code, SecuritySession session) throws BusinessError;
	/**
	 * 只查询主表的数据
	 * @param code
	 * @param param
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	List<JSONObject> fetchMainData(String code, JSONObject param, SecuritySession client) throws BusinessError;
	/**
	 * 
	 * @param code
	 * @param data_id
	 * @param includeSlave
	 * @param client
	 * @return
	 * @throws BusinessError
	 */
	JSONObject findBillData(String code, String data_id, Boolean includeSlave, SecuritySession client) throws BusinessError;
}
