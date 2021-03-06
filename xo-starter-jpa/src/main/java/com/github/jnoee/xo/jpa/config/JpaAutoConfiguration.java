package com.github.jnoee.xo.jpa.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.jnoee.xo.jpa.cache.EntityCacheManager;
import com.github.jnoee.xo.jpa.dao.DaoUtils;
import com.github.jnoee.xo.jpa.entity.StringToUuidEntity;
import com.github.jnoee.xo.jpa.entity.UuidEntityToString;

@Configuration
@AutoConfigureAfter(value = JpaRepositoriesAutoConfiguration.class)
@EntityScan({"com.github.jnoee.xo.jpa.converter", "com.github.jnoee.xo.jpa.usertype"})
public class JpaAutoConfiguration implements WebMvcConfigurer {
  @Bean
  EntityCacheManager entityCacheManager() {
    return new EntityCacheManager();
  }

  /**
   * 当开启了查询缓存时，构建查询缓存配置。
   * 
   * @return 返回查询缓存配置。
   */
  @Bean("com.github.jnoee.xo.jpa.config.CacheSettings")
  @ConditionalOnProperty(value = "spring.jpa.properties.hibernate.cache.use_query_cache",
      havingValue = "true", matchIfMissing = false)
  CacheSettings cacheSettings() {
    return new CacheSettings();
  }

  @Bean
  DaoUtils daoUtils() {
    return new DaoUtils();
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverterFactory(new StringToUuidEntity());
    registry.addConverter(new UuidEntityToString());
  }
}
