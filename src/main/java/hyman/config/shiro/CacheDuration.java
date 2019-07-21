package hyman.config.shiro;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义缓存持续时间

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CacheDuration {

    // Sets the expire time (in seconds).
    public long duration() default 0;
}
