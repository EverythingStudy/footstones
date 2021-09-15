package com.whw.basic.config.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Description TODO Redis五种数据结构：1.String 2.Map 3.List 4.Set 5.Sorted Set
 * TODO 默认使用lettuce客户端程序
 * @Date 2020/7/1 10:41
 * @Author cly
 **/
@Service
public class RedisService {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static final Long SUCCESS = 1L;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @return void
     * @Author cly
     * @Description //TODO 添加hash,需要hash名和存储的键值对Map
     * @Date 15:44 2020/7/1
     * @Param [hashName, map]
     **/
    public void setHash(String hashName, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(hashName, map);
    }

    /**
     * @return void
     * @Author cly
     * @Description //TODO 根据hash 中key的值不存在则添加
     * @Date 15:41 2020/7/1
     * @Param [hashName, key, value]
     **/
    public void putIfAbsent(String hashName, String key, String value) {
        redisTemplate.opsForHash().putIfAbsent(hashName, key, value);
    }

    /**
     * @return void
     * @Author cly
     * @Description //TODO 1.添加hash中元素 2.修改hash中元素
     * @Date 16:01 2020/7/1
     * @Param [hashName, key, value]
     **/
    public void put(String hashName, String key, String value) {
        redisTemplate.opsForHash().put(hashName, key, value);
    }

    /**
     * @return java.lang.Object
     * @Author cly
     * @Description //TODO hash中key查询
     * @Date 16:13 2020/7/1
     * @Param [hashName, key]
     **/
    public Object getHash(String hashName, String key) {
        return redisTemplate.opsForHash().get(hashName, key);
    }

    /**
     * @return void
     * @Author cly
     * @Description //TODO 设置key超时时间
     * @Date 16:37 2020/7/1
     * @Param [hashName, time, timeUnit]
     **/
    public void setKeyTime(String hashName, Long time, TimeUnit timeUnit) {
        boolean flag = redisTemplate.expire(hashName, time, timeUnit);
        System.out.println(flag);
    }

    //Springboot的启动器main方法上需要加上@EnableCaching开启缓存，使用了@Cacheable注解后，缓存的值将被存入redis数据库中
    //缓存名可以为RedisConfig中自定义的缓存名，键生成器为RedisConig中自定义的键生成器，也可以自己自定义缓存key名
    //@Cacheable(cacheNames = "users",keyGenerator ="myKeyGenerator")
    //从redis中获取map
    public Map<Object, Object> getHash(String hashName) {
        if (redisTemplate.hasKey(hashName)) {
            System.out.println(redisTemplate.opsForHash().entries(hashName));
            return redisTemplate.opsForHash().entries(hashName);
        } else {
            return null;
        }
    }

    /**
     * @return void
     * @Author cly
     * @Description //TODO String字符串结构
     * @Date 17:28 2020/7/1
     * @Param [key, value]
     **/
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * @return boolean
     * @Author cly
     * @Description //TODO String 字符串,如果不存在放入并设置过期时间
     * @Date 18:03 2020/7/1
     * @Param [key, requestId, time, timeUnit]
     **/
    public boolean set(String key, String requestId, Long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, requestId, time, timeUnit);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 147
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * @return boolean
     * @Author cly
     * @Description //TODO 获取分布式锁，expireTime为数据时间（秒），waitTimeout为获取锁的过期时间（毫秒）
     * @Date 15:31 2020/7/2
     * @Param [lockKey, requestId, expireTime, waitTimeout]
     **/
    public boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        // 当前时间
        long nanoTime = System.nanoTime();
        try {
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
            logger.info("开始获取分布式锁-key[{}]", lockKey);
            int count = 0;
            do {
                RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
                logger.debug("尝试获取分布式锁-key[{}]requestId[{}]count[{}]", lockKey, requestId, count);
                Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId, expireTime);
                if (result.equals(1L)) {
                    logger.debug("尝试获取分布式锁-key[{}]成功", lockKey);
                    return true;
                }
                Thread.sleep(500L);//休眠500毫秒
                count++;
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        } catch (Exception e) {
            logger.error("尝试获取分布式锁-key[{}]异常", lockKey);
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @return boolean
     * @Author cly
     * @Description //TODO 释放分布式锁锁
     * @Date 14:38 2020/7/2
     * @Param [lockKey, requestId]
     **/
    public boolean releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * @return boolean
     * @Author cly
     * @Description //TODO 判断key是否存在
     * @Date 18:02 2020/7/2
     * @Param [key]
     **/
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
