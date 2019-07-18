package hyman.aop;

import java.lang.annotation.*;

/**
 * 功能模块名称：自定义注解记录系统日志
 *
 * 文件名称为：SystemLog.java 文件创建人：徐广成
 *
 * 修改记录： 修改人 修改日期 备注
 *
 * @author xuguangcheng
 * @version
 * @time 2018年5月3日 上午11:26:48
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface CmeSystemLog {
    /**
     * 业务主体
     *
     * @return
     */
    String businessBody() default "";

    /**
     * 业务主体编码
     *
     * @return
     */
    String businessBodyCode() default "";

    /**
     * 业务动作编码
     *
     * @return
     */
    String actionCode() default "PLAN-0";


    /**
     * 业务动作名称
     *
     * @return
     */
    String actionName() default "计划管理";
}
