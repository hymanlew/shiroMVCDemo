package hyman.config.shiro;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

public class ShiroRedisCacheManager extends AbstractCacheManager {

    /**
     * redis 模板
     */
    private RedisTemplate<byte[], Object> redisTemplate;

    public ShiroRedisCacheManager(RedisTemplate<byte[], Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Cache<byte[], Object> createCache(String name) throws CacheException {
        return new ShiroRedisCache<byte[], Object>(redisTemplate, name);
    }
}
