package net.popbean.pf.cache.service.impl;

import java.util.Collection;

import net.popbean.pf.business.service.impl.AbstractBusinessService;
import net.popbean.pf.cache.service.CacheManagementBusinessService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

/**
 * 缓存管理
 * @author to0ld
 *
 */
@Service("service/pf/cache/redis")
public class CacheManagementBusinessServiceRedisImpl extends AbstractBusinessService implements CacheManagementBusinessService {
	//
	@Autowired
	@Qualifier("cacheManager")
	CacheManager cacheManager;
	//
	/**
	 * 有cache就一定要提供人工evict的工具
	 */
	@Override
	public void evict(String cache_code, String key) {
		RedisCache rc = (RedisCache)cacheManager.getCache(cache_code);
		if(StringUtils.isBlank(key)){//清除整个cache的行为
			rc.clear();
		}else{
			rc.evict(key);
		}
	}
	/**
	 * 获得指定的cache信息
	 * @param name
	 * @return
	 */
	@Override
	public Collection<String> fetchAllCacheName(){
		return cacheManager.getCacheNames();
	}
}
