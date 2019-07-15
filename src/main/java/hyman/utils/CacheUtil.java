package hyman.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheUtil {

    private static CacheManager cacheManager;

    static{
        ClassLoader laoder = CacheUtil.class.getClassLoader();
        cacheManager = CacheManager.create(laoder.getResourceAsStream("ehcache.xml"));
    }

    public static void put(String cacheName, String key, Object value){
        // 获取指定的缓存空间，例如 authenticationCache 登录验证缓存
        Cache cache = cacheManager.getCache(cacheName );
        // 	存储数据到 cache 元素
        Element element = new Element(key, value);
        cache.put(element);
    }

    public static Object get(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();
    }

    // 退出时清除用户的权限
    public static void remove(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.remove(key);
    }
}
