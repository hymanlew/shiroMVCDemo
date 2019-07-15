package hyman.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.UUID;

// 防重复提交拦截器
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private MessageSourceAccessor msa;

    /**
     * 防止页面重复提交token标识
     */
    private final String token = "token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                boolean needSaveSession = annotation.save();
                if (needSaveSession) {
                    HttpSession session = request.getSession(false);
                    if (session == null) {
                        session = request.getSession(true);
                    }
                    session.setAttribute(token, UUID.randomUUID());
                }
                boolean needRemoveSession = annotation.remove();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        ResponseBody respBody = method.getAnnotation
                                (ResponseBody.class);
                        if (respBody != null) {
                            response.getWriter()
                                    .write("{\"state\":0,\"message\":\"" + msa.getMessage("sys.data.resubmit") + "\"}");
                        } else {
                            response.sendRedirect(
                                    "error?message=" +
                                            URLEncoder.encode(msa.getMessage("sys.data.resubmit"), "utf-8"));
                        }
                        return false;
                    }
                    // request.getSession(false).removeAttribute(token);
                }
            }

            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    // @return 是否重复提交 true 是 false 否
    private boolean isRepeatSubmit(HttpServletRequest request) {
        Object tokenObj = request.getSession().getAttribute(token);
        if (tokenObj == null) {
            return true;
        }
        String serverToken = (String) tokenObj;
        if (!StringUtils.isNotBlank(serverToken)) {
            return true;
        }
        String clinetToken = request.getParameter(token);
        if (clinetToken == null) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }
}
