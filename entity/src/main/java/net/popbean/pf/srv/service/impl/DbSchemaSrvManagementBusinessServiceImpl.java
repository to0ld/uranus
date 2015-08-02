package net.popbean.pf.srv.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.entity.IValueObject;
import net.popbean.pf.entity.field.annotation.Entity;
import net.popbean.pf.entity.model.EntityModel;
import net.popbean.pf.entity.model.helper.EntityModelHelper;
import net.popbean.pf.entity.service.EntityStructBusinessService;
import net.popbean.pf.exception.BusinessError;
import net.popbean.pf.security.vo.SecuritySession;
import net.popbean.pf.srv.service.SrvManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
@Service("service/pf/srv/dbschema")
public class DbSchemaSrvManagementBusinessServiceImpl extends AbstractBusinessService implements SrvManagementBusinessService {
	@Autowired
	@Qualifier("service/pf/entity/struct")
	EntityStructBusinessService esService;
	//
	@Override
	public String exp(String version, SecuritySession client) throws BusinessError {
		return null;
	}
	/**
	 * 
	 */
	@Override
	public String imp(String version, JSONObject param ,SecuritySession client) throws BusinessError {
		try {
			//从参数中获得扫描路径
			String package_pref = (param == null)?null:param.getString("package");
			//
			List<String> clazz_list = new ArrayList<String>();
			//
			if(!StringUtils.isBlank(package_pref)){
				String[] tmps = package_pref.split(";");
				for(String tmp : tmps){
					List<String> list = scanEntity(tmp);
					if(!CollectionUtils.isEmpty(list)){
						clazz_list.addAll(list);
					}
				}
			}else{
				clazz_list = scanEntity(package_pref);
			}
			log.info("scan result:"+JSONObject.toJSONString(clazz_list));
			esService.syncDbStructByClazz(clazz_list, client);//建表
			
			List<EntityModel> em_list = new ArrayList<>();
			
			for(String clazz:clazz_list){
				EntityModel em = EntityModelHelper.build((Class<IValueObject>)Class.forName(clazz));
				em_list.add(em);
			}
			esService.syncEntityModel(em_list, client);
			return "success";
		} catch (Exception e) {
			processError(e);
		}
		return null;
	}
	/**
	 *  扫描实体类(带有annotation并且有值的)
	 * @param package_pref
	 * @return
	 * @throws BusinessError
	 */
	private List<String> scanEntity(String package_pref)throws BusinessError{//扫描指定包的meta
		List<String> ret = new ArrayList<>(); 
		ClassPathScanningCandidateComponentProvider scanner=new ClassPathScanningCandidateComponentProvider(false);
		//
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
//         scanner.addIncludeFilter(new AssignableTypeFilter(TableMeta.class));//如果要找基类就这么干
         
		Set<BeanDefinition> candidate_list = scanner.findCandidateComponents(package_pref);
         for (BeanDefinition candidate : candidate_list) {
        	 if(candidate.isAbstract()){//干掉抽象类，不会有人在接口上定义吧
        		 continue;
        	 }
             Class cls = ClassUtils.resolveClassName(candidate.getBeanClassName(),scanner.getResourceLoader().getClassLoader());
             ret.add(cls.getName());
         }
         return ret;
	}
}
