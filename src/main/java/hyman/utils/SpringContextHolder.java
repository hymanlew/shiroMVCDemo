package hyman.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

// 以静态变量保存 Spring ApplicationContext, 可在任何代码任何地方任何时候中取出 ApplicaitonContext.
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * 声明applicationContext
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     * @param applicationContext applicationContext对象
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * <p><b>方法描述：</b><b>取得存储在静态变量中的ApplicationContext.</b></p>
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * <p><b>方法描述：</b><b>从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. </b></p>
     * @param name bean名称
     * @param <T> 泛型
     * @return bean实体
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * <p><b>方法描述：</b><b>从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. </b></p>
     * @param clazz bean类型
     * @param <T> 泛型
     * @return bean实体
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return (T) applicationContext.getBeansOfType(clazz);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * <p><b>方法描述：</b><b>验证是否注入</b></p>
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
