package hyman.interceptor;

import hyman.utils.Constant;
import hyman.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLEncoder;

public class ExceptionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInterceptor.class);
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        String errorMessage = "";
        if (ex != null) {
            if (ex instanceof NullPointerException) {
                errorMessage = ex.toString();
            } else if (ex.getCause() != null) {
                errorMessage = ex.getCause().getMessage().trim();
            } else {
                errorMessage = ex.getMessage();
            }
            String loginId = UserUtils.getCurrentUserId();
            String ip = "";
            if (Constant.PROXY_IP_NAME.isEmpty()) {
                ip = request.getRemoteAddr();
            } else {
                ip = request.getHeader(Constant.PROXY_IP_NAME);
            }
            LOGGER.error("loginId:" + loginId + ",ip:" + ip + ",errorMessage=" + errorMessage + ",uri:" + request.getRequestURL().toString());
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                ResponseBody respBody = method.getAnnotation(ResponseBody.class);
                if (respBody != null) {
                    response.getWriter().write("{\"state\":0,\"message\":\"" + errorMessage + "\"}");
                } else {
                    response.sendRedirect("error?message=" + URLEncoder.encode(errorMessage, "utf-8"));
                }
            }
        }
    }
}
