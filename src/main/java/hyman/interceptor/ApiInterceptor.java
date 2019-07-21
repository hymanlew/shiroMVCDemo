package hyman.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

// api 计时拦截器
public class ApiInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> STARTTIME_THREAD_LOCAL = new NamedThreadLocal<Long>("ThreadLocal StartTime");
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {

        if (LOGGER.isDebugEnabled()) {
            long beginTime = System.currentTimeMillis(); // 1、开始时间
            STARTTIME_THREAD_LOCAL.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
            LOGGER.debug("开始计时: {}  URI: {}", new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime),
                    httpServletRequest.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            LOGGER.info("ViewName: " + modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e)
            throws Exception {

        // 打印JVM信息。
        if (LOGGER.isDebugEnabled()) {

            long beginTime = STARTTIME_THREAD_LOCAL.get(); // 得到线程绑定的局部变量（开始时间）
            long endTime = System.currentTimeMillis(); // 2、结束时间

            LOGGER.debug("计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  " +
                            "最大可用内存: {}m",
                    new SimpleDateFormat("hh:mm:ss.SSS").format(endTime),
                    //DateUtils.formatDateTime(endTime - beginTime),
                    request.getRequestURI(), Runtime.getRuntime().maxMemory() / 1024 / 1024,
                    Runtime.getRuntime().totalMemory() / 1024 / 1024,
                    Runtime.getRuntime().freeMemory() / 1024 / 1024,

                    (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024);

            STARTTIME_THREAD_LOCAL.remove();
        }
    }
}
