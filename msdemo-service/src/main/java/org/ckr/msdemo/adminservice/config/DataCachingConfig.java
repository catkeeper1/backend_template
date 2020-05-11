package org.ckr.msdemo.adminservice.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Config data caching.
 * Setup redis access for data caching. Provide a instance of {@link RedisTemplate} to access Redis.
 */
@Configuration
@EnableConfigurationProperties(DataCachingConfig.class)
@ConfigurationProperties(prefix = "app.cache.redis")
public class DataCachingConfig {

    private String host;
    private Integer port;
    private String password;


    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public LettuceConnectionFactory cacheRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        if(password != null && password.trim().length() > 0) {
            config.setPassword(RedisPassword.of(password));
        }

        return new LettuceConnectionFactory(config);
    }


    @Bean
    public RedisSerializer<Object> cacheRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisTemplate cacheRedisTemplate() {

        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(cacheRedisSerializer());
        template.setEnableDefaultSerializer(true);
        template.setConnectionFactory(cacheRedisConnectionFactory());

        return template;

    }


    /**
     * Enable spring caching service so that method with {@link org.springframework.cache.annotation.Cacheable}
     * annotation can access cache service.
     *
     */
    @Configuration
    @EnableCaching
    public static class CacheServiceConfig extends CachingConfigurerSupport {

        @Autowired
        DataCachingConfig dataCachingConfig;

        @Bean
        @Override
        public CacheManager cacheManager() {
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig();
            defaultConfig = defaultConfig.entryTtl(Duration.ofSeconds(300));

            RedisSerializationContext.SerializationPair<Object> sePair =
                    RedisSerializationContext.SerializationPair
                                             .fromSerializer(dataCachingConfig.cacheRedisSerializer());

            defaultConfig = defaultConfig.serializeValuesWith(sePair);


            CacheManager cacheManager =
                    RedisCacheManager.builder(dataCachingConfig.cacheRedisConnectionFactory())
                            .cacheDefaults(defaultConfig)
                            .build();

            return cacheManager;
        }
    }
}