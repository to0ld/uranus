package net.popbean.pf.bill.service;

import net.popbean.pf.bill.vo.BillModel;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

public interface BillModelBusinessService {
	/**
	 * 根据单据编码和stage来定位模型,并根据默认值的设定提取数据
	 * 
	 * @param code
	 * @param stage
	 *            单据的场景，相当于分类,允许为空
	 * @param hasData
	 *            是否带默认值
	 * @param session
	 * @return
	 * @throws BusinessError
	 */
	public BillModel find(String code, String stage, boolean hasData, SecuritySession session) throws BusinessError;

	/**
	 * 保存模型 如果不齐备不能刷新缓存 不发布也不能
	 * 
	 * @param model
	 * @param session
	 * @throws BusinessError
	 */
	public void save(BillModel model, SecuritySession session) throws BusinessError;

	/**
	 * 刷新bill model(语义上强制刷新吧)
	 * 
	 * @param code
	 * @param stage
	 * @param session
	 * @throws BusinessError
	 */
	public void flush(String code, String stage,SecuritySession session) throws BusinessError;

	/**
	 * 根据对象的唯一标志来找真实的model 先找主表数据，然后再根据billevent信息来定位
	 * 
	 * @param code
	 * @param id
	 * @param hasData
	 * @param session
	 * @return
	 * @throws BusinessError
	 */
	public BillModel findById(String code, String id, boolean hasData, SecuritySession session) throws BusinessError;
}
