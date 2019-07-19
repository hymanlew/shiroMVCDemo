package hyman.config;

import hyman.utils.SerializeUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;

/**
 * <p><b>类描述：</b>shiro缓存工具类</p>
 * @param <K>
 * @param <V>
 */
public class ShiroRedisCache<K, V> implements Cache<K, V> {

    /**
     * redis 模板
     */
    private RedisTemplate<byte[], V> redisTemplate;
    /**
     * 前置标识符
     */
    private String prefix = "shiro_redis:";

    public ShiroRedisCache(RedisTemplate<byte[], V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ShiroRedisCache(RedisTemplate<byte[], V> redisTemplate, String prefix) {
        this(redisTemplate);
        this.prefix = prefix;
    }

    /**
     * 获取缓存值
     *
     * @param key
     *            key
     * @return V 泛型对象
     * @throws CacheException 缓存操作异常
     */
    @Override
    public V get(K key) throws CacheException {
        if (key == null) {
            return null;
        }

        byte[] bkey = getByteKey(key);
        return redisTemplate.opsForValue().get(bkey);
    }

    /**
     * 设置缓存值
     * @param key key
     * @param value value
     * @return V 值
     * @throws CacheException 缓存操作异常
     */
    @Override
    public V put(K key, V value) throws CacheException {
        if (key == null || value == null) {
            return null;
        }

        byte[] bkey = getByteKey(key);
        redisTemplate.opsForValue().set(bkey, value);
        return value;
    }

    /**
     * 根据key移除数据
     * @param key key
     * @return V 值
     * @throws CacheException 缓存操作异常
     */
    @Override
    public V remove(K key) throws CacheException {
        if (key == null) {
            return null;
        }

        byte[] bkey = getByteKey(key);
        ValueOperations<byte[], V> vo = redisTemplate.opsForValue();
        V value = vo.get(bkey);
        redisTemplate.delete(bkey);
        return value;
    }

    /**
     * 清除缓存
     * @throws CacheException 缓存操作异常
     */
    @Override
    public void clear() throws CacheException {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    /**
     * 获取链接数
     * @return 链接数
     */
    @Override
    public int size() {
        Long len = redisTemplate.getConnectionFactory().getConnection().dbSize();
        return len.intValue();
    }

    /**
     * 获取所有值
     * @return Set 所有key
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        byte[] bkey = (prefix + "*").getBytes();
        Set<byte[]> set = redisTemplate.keys(bkey);
        Set<K> result = new HashSet();

        if (set.size() == 0) {
            return Collections.emptySet();
        }

        for (byte[] key : set) {
            result.add((K) key);
        }
        return result;
    }

    /**
     * 获取所有值
     * @return Collection 所有value
     */
    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        List<V> values = new ArrayList<V>(keys.size());
        for (K k : keys) {
            byte[] bkey = getByteKey(k);
            values.add(redisTemplate.opsForValue().get(bkey));
        }
        return values;
    }

    /**
     *
     * <p><b>方法描述：</b>根据key获取byte[]</p>
     * @param key key
     * @return byte[]
     */
    private byte[] getByteKey(K key) {
        if (key instanceof String) {
            String preKey = this.prefix + key;
            return preKey.getBytes();
        } else {
            return SerializeUtils.serialize(key);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
