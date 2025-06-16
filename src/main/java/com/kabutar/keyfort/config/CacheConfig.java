package com.kabutar.keyfort.config;

import java.util.concurrent.TimeUnit;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.spring.starter.embedded.InfinispanCacheConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kabutar.keyfort.constant.CacheConstant;

@Configuration
public class CacheConfig {
	
	@Bean
	@ConditionalOnProperty(name = "cache.type", havingValue = "INF-EMB")
	public InfinispanCacheConfigurer cacheConfigurer() {
		return cacheManager -> {
			ConfigurationBuilder config = new ConfigurationBuilder();
			config.expiration().lifespan(300,TimeUnit.SECONDS);
			cacheManager.defineConfiguration(CacheConstant.CacheStore.PKCE, config.build());
		};
		
	}

}
