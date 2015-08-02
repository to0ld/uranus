package net.popbean.pf.app.helper;

import net.popbean.pf.app.vo.AppConfigVO;
import net.popbean.pf.helper.IOHelper;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class AppHelper {
	/**
	 * 
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public static AppConfigVO loadConfig(String appkey,String version)throws Exception{
		String path = "classpath:/data/app/"+appkey+"/install/cfg.data";//如果是安装的情况
		if(!StringUtils.isBlank(version)){
			path = "classpath:/data/app/"+appkey+"/patch/"+version+"/cfg.data";
		} 
		String content = IOHelper.readByChar(path, "utf-8");
		JSONObject jo = JSON.parseObject(content);
		AppConfigVO inst = jo.getObject("data", AppConfigVO.class);
		return inst;
	}

}
