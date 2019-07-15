package hyman.security;

import hyman.utils.SerializableUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.SerializationUtils;

import java.util.*;

public class ShiroRedisCache<K, V> implements Cache<K, V> {

    private RedisTemplate<byte[], V> redisTemplate;
    private String prefix = "shiro_redis:";


    public ShiroRedisCache(RedisTemplate<byte[], V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ShiroRedisCache(RedisTemplate<byte[], V> redisTemplate, String prefix) {
        this(redisTemplate);
        this.prefix = prefix;
    }

    @Override
    public V get(K k) throws CacheException {
        if (k == null) {
            return null;
        }
        byte[] bkey = getByteKey(k);
        return redisTemplate.opsForValue().get(bkey);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        if (k == null || v == null) {
            return null;
        }
        byte[] bkey = getByteKey(k);
        redisTemplate.opsForValue().set(bkey, v);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        if (k == null) {
            return null;
        }

        byte[] bkey = getByteKey(k);
        ValueOperations<byte[], V> vo = redisTemplate.opsForValue();
        V value = vo.get(bkey);
        redisTemplate.delete(bkey);
        return value;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        Long len = redisTemplate.getConnectionFactory().getConnection().dbSize();
        return len.intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<K> keys() {
        byte[] bkey = (prefix + "*").getBytes();
        Set<byte[]> set = redisTemplate.keys(bkey);
        Set<K> result = new HashSet();

        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }

        for (byte[] key : set) {
            result.add((K) key);
        }
        return result;
    }

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


    private byte[] getByteKey(K key) {
        if (key instanceof String) {
            String preKey = this.prefix + key;
            return preKey.getBytes();
        } else {
            //org.apache.commons.lang3.SerializationUtils.serialize();
            //return SerializableUtils.protoSerialize(key);
            return null;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
