package net.popbean.pf.schedule.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.popbean.pf.config.service.ConfigBusinessService;
import net.popbean.pf.config.vo.IConfigValueStore;
import net.popbean.pf.entity.helper.JO;
import net.popbean.pf.entity.helper.JOHelper;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.schedule.service.ScheduleBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 如果有必要就加上前后置的log记录
 * 
 * @author to0ld
 * 
 */
public abstract class AbstractJob implements Job {

	private ApplicationContext appContext;
	protected static Logger log = Logger.getLogger("SERVICE");// 这个将来要换成别的logger

	//
	protected ApplicationContext getAppCtx(JobExecutionContext context) throws Exception {// 其实可以考虑全局，单例的
		ApplicationContext appctx = (ApplicationContext) context.getScheduler().getContext().get("appctx");// appctx在bean的文件中定义
		return appctx;
	}

	protected <T> T getSpringBean(String beanId) throws Exception {
		return (T) appContext.getBean(beanId);
	}

	protected <T> T getSpringBean(String beanId, Class<T> clazz) throws Exception {
		return appContext.getBean(beanId, clazz);
	}

	abstract protected void procBusiness(JobExecutionContext context, JSONObject param) throws BusinessError;
	/**
	 * 从哪里获得参数呢？需要具备什么样的参数呢？
	 * @param context
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		/**
		JobDataMap dataMap = null;
		ScheduleBusinessService smService = null;
		JobKey key = context.getJobDetail().getKey();
		String job_name = key.getName();
		String job_group = key.getGroup();
		JSONObject logVo = JO.gen("job_name", job_name, "job_group", job_group);
		JSONObject param = JO.gen("JOB_NAME", job_name, "JOB_GROUP", job_group);
		try {
			this.appContext = getAppCtx(context);
			//
			smService = getSpringBean("service/pf/schedule", ScheduleBusinessService.class);
			JSONObject inst = smService.findJob(param);//FIXME 这个应该找到job model
			//
			dataMap = context.getJobDetail().getJobDataMap();
			param.putAll(dataMap);
			String param_code = inst.getString("CFG_CODE");// 获得配置
			String app_code = inst.getString("APP_CODE");
			//
			if (!StringUtils.isEmpty(param_code)) {
				try {
					ConfigBusinessService configService = this.getSpringBean("service/pf/config/redis");
					IConfigValueStore param_ = configService.findValue(app_code, param_code, null);
					if (param_ != null) {//合并参数
						JSONObject tmp = JSON.parseObject(JSON.toJSONString(param_));
						param.putAll(tmp);
					}
				} catch (Exception e) {
					log.error(e);// 至少要记录一下错误
				}
			}
			Timestamp stamp = inst.getTimestamp("LAST_EXEC_TS");// 上一次成功启动的时间

			Timestamp startTs = smService.startJobExecute(param, null);// mark一下
			if (startTs == null) {
				logVo.put("op_cate", 0);
				logVo.put("error_msg", "启动任务时失败。");
			} else {
				logVo.put("current_exec_start_ts", startTs);
				param.put("LAST_EXEC_TS", stamp);
				log.debug("<---" + JSON.toJSONString(param));

				procBusiness(context, param);// 执行业务
				Timestamp endTs = smService.endJobExecute(param, null);

				JOHelper.copy(vo, logVo);
				if (endTs == null) {
					logVo.put("opt_cate", 0);
					logVo.put("error_msg", "结束任务时失败。");
				} else {
					logVo.put("current_exec_end_ts", endTs);
					logVo.put("op_cate", 3);
				}
			}
		} catch (Exception e) {
			JSONObject vo = ProfLogHelper.endVO(this.getClass().getSimpleName(), null);
			try {
				JOHelper.copy(vo, logVo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			logVo.put("op_cate", 0);
			logVo.put("error_msg", e.getMessage());
			log.error(e);
			throw new JobExecutionException("JOB_EXCUTE_ERROR", e);
		} finally {
		}**/
	}
}
