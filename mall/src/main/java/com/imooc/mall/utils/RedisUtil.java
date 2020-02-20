package com.imooc.mall.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;
    public void hset(String key,String hkey,String hvalue)
    {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.put(key, hkey, hvalue);
    }
    public String hget(String key,Object hkey)
    {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hkey);
    }
    public Map<String, String> getMap(String key)
    {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.entries(key);
    }
    public void delete(String key,Object hkey)
    {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(key,hkey);

    }

}
