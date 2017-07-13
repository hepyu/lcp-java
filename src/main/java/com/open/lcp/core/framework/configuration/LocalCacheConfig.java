package com.open.lcp.core.framework.configuration;

import java.util.HashSet;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

public class LocalCacheConfig {
	
	public static final String CACHE_TOP_CHIP_USERS = "Fs.Cache.TopChipUsers";
	
	private static final String[] cacheNames = new String[]{CACHE_TOP_CHIP_USERS};

	@Bean
	public CacheManager createGuavaCacheManager() {
		SimpleCacheManager manager =  new SimpleCacheManager();
		
		HashSet<Cache> caches = new HashSet<Cache>();
		
		for(String cacheName : cacheNames) {
		
			ConcurrentMapCache cache = new ConcurrentMapCache(cacheName);			
			caches.add(cache);		
		}
		
		manager.setCaches(caches);
		
		return manager;
	}
	
}
