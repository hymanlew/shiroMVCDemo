package hyman.aop;

import java.lang.annotation.*;

/**
 * 功能模块名称：自定义注解记录系统日志
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface LogAnnotation {

    /**
     * 业务主体
     */
    String businessBody() default "";

    /**
     * 业务主体编码
     */
    String businessBodyCode() default "";

    /**
     * 业务动作编码
     */
    String actionCode() default "PLAN-0";


    /**
     * 业务动作名称
     */
    String actionName() default "业务名称";
}
