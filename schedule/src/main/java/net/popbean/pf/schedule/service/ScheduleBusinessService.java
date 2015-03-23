package net.popbean.pf.schedule.service;

import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;

/**
 * 
 * @author to0ld
 * 
 */
public interface ScheduleBusinessService {
	/**
	 * 保存job
	 * 
	 * @param job
	 * @param client
	 * @throws BuzException
	 */
	void saveJob(JSONObject job, SecuritySession client) throws BusinessError;

	/**
	 * 通过PK值删除job
	 * 
	 * @param pk_job
	 * @param client
	 * @throws BuzException
	 */
	void deleteJob(String pk_job, SecuritySession client) throws BusinessError;

	/**
	 * 通过名字和group删除job
	 * 
	 * @param job_name
	 * @param job_group
	 * @param client
	 * @throws BuzException
	 */
	void deleteJob(String job_name, String job_group, SecuritySession client) throws BusinessError;

	/**
	 * 更新job,如果有相同的job_name和job_group,前一个将会删掉重建
	 * 
	 * @param param
	 * @param client
	 * @throws BuzException
	 */
	void updateJob(JSONObject param, SecuritySession client) throws BusinessError;

	/**
	 * 更改job状态
	 * 
	 * @param pk_job_list
	 * @param new_stat
	 * @param client
	 * @throws BuzException
	 */
	void changeStat(List<String> pk_job_list, Integer new_stat, SecuritySession client) throws BusinessError;

	/**
	 * 启动调度服务
	 * 
	 * @throws BuzException
	 */
	void start() throws BusinessError;

	/**
	 * 关闭调度服务
	 * 
	 * @throws BuzException
	 */
	void shutdown() throws BusinessError;

	/**
	 * 查看存储日志
	 * 
	 * @param param
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	public List<JSONObject> fetchLog(JSONObject param, SecuritySession client) throws BusinessError;// 转移到scheduleservice中

	/**
	 * 找上一次执行的开始时间
	 * 
	 * @param param
	 * @param client
	 * @return
	 * @throws BuzException
	 */
	public Timestamp findLastExecTime(JSONObject param, SecuritySession client) throws BusinessError;

	public void execute(JSONObject context, SecuritySession client) throws BusinessError;

	/**
	 * 立即开始job
	 * 
	 * @param pk_job
	 * @param client
	 * @throws BuzException
	 */
	void startJob(String pk_job, SecuritySession client) throws BusinessError;

	/**
	 * 立即开始job
	 * 
	 * @param job_name
	 * @param job_group
	 * @param client
	 * @throws BuzException
	 */
	void startJob(String job_name, String job_group, SecuritySession client) throws BusinessError;

	/**
	 * 立即停止job
	 * 
	 * @param pk_job
	 * @param client
	 * @throws BuzException
	 */
	void stopJob(String pk_job, SecuritySession client) throws BusinessError;
	/**
	 * 得到job
	 * @param param
	 * @return
	 * @throws BuzException
	 */
	public JSONObject findJob(JSONObject param) throws BusinessError;

	Timestamp startJobExecute(JSONObject param, SecuritySession client) throws BusinessError;

	Timestamp endJobExecute(JSONObject param, SecuritySession client) throws BusinessError;
}
