package net.popbean.pf.cache.service;

import java.util.Collection;

/**
 * 缓存管理
 * @author to0ld
 *
 */
public interface CacheManagementBusinessService {
	public void evict(String cache_code,String key);

	Collection<String> fetchAllCacheName();
}
