package com.whw.footstones.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description Redis配置
 * @Date 2020/7/1 10:04
 * @Author cly
 **/
@Configuration
public class RedisConfig {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    /**
     * lettuce客户端连接工厂
     */
    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;
    /**
     * json序列化器
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    /**
     * 缓存生存时间
     */
    private Duration timeToLive = Duration.ofDays(1);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //redis缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(this.timeToLive)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
        //缓存配置map
        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        //自定义缓存名，后面使用的@Cacheable的CacheName
        cacheConfigurationMap.put("users", config);
        cacheConfigurationMap.put("default", config);
        //根据redis缓存配置和reid连接工厂生成redis缓存管理器
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .build();
        logger.debug("自定义RedisCacheManager加载完成");
        return redisCacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        // key序列化
        redisTemplate.setKeySerializer(keySerializer());
        // value序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key序列化
        redisTemplate.setHashKeySerializer(keySerializer());
        // Hash value序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * @return org.springframework.data.redis.serializer.RedisSerializer<java.lang.String>
     * @Author cly
     * @Description //TODO redis键序列化使用StrngRedisSerializer
     * @Date 10:24 2020/7/1
     * @Param []
     **/
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * @return
     * @Author cly
     * @Description //TODO 缓存键自动生成器
     * @Date 16:52 2020/9/17
     * @Param
     **/
    @Bean
    public KeyGenerator myKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}
