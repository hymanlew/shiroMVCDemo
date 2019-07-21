package hyman.config.shiro;

import hyman.aop.LogAnnotation;
import hyman.config.shiro.CacheDuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.ReflectionUtils;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.StringUtils.isEmpty;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpringRedisCacheManager extends RedisCacheManager implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    public SpringRedisCacheManager(RedisOperations<?, ?> redisOperations) {
        super(redisOperations);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        parseCacheDuration(applicationContext);
    }

    private void parseCacheDuration(ApplicationContext applicationContext) {
        final Map<String, Long> cacheExpires = new HashMap<String, Long>();
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            final Class<?> clazz = applicationContext.getType(beanName);
            LogAnnotation service = findAnnotation(clazz, LogAnnotation.class);
            if (null == service) {
                continue;
            }
            addCacheExpires(clazz, cacheExpires);
        }
        // 设置有效期
        super.setExpires(cacheExpires);
    }

    private void addCacheExpires(final Class<?> clazz, final Map<String, Long> cacheExpires) {
        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(method);
                CacheDuration cacheDuration = findCacheDuration(clazz, method);
                Cacheable cacheable = findAnnotation(method, Cacheable.class);
                CacheConfig cacheConfig = findAnnotation(clazz, CacheConfig.class);
                Set<String> cacheNames = findCacheNames(cacheConfig, cacheable);
                for (String cacheName : cacheNames) {
                    cacheExpires.put(cacheName, cacheDuration == null ? 0 : cacheDuration.duration());
                }
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return null != findAnnotation(method, Cacheable.class);
            }
        });
    }

    /**
     * CacheDuration标注的有效期，优先使用方法上标注的有效期
     *
     * @param clazz
     * @param method
     * @return
     */
    private CacheDuration findCacheDuration(Class<?> clazz, Method method) {
        CacheDuration methodCacheDuration = findAnnotation(method, CacheDuration.class);
        if (null != methodCacheDuration) {
            return methodCacheDuration;
        }

        CacheDuration classCacheDuration = findAnnotation(clazz, CacheDuration.class);
        if (null != classCacheDuration) {
            return classCacheDuration;
        }

        return methodCacheDuration;
    }

    private Set<String> findCacheNames(CacheConfig cacheConfig, Cacheable cacheable) {

        Set<String> nameset = new HashSet<>();
        for(String name : cacheConfig.cacheNames()){
            nameset.add(name);
        }

        Set<String> valueset = new HashSet<>();
        for(String value : cacheable.value()){
            valueset.add(value);
        }

        return isEmpty(cacheable.value()) ? nameset : valueset;
    }
}
