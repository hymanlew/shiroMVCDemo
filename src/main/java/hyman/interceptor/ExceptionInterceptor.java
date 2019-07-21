package hyman.interceptor;

import hyman.utils.Constant;
import hyman.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 官方参考实现是hibernate Validator（与Hibernate ORM 没有关系），JSR 303 用于对Java Bean 中的字段的值进行验证。
 *
 * 配置信息，默认在 ValidationMessages.properties 配置文件中。在 boot 中名字必须为 “ValidationMessages.properties“，因为
 * SpringBoot 自动读取 classpath 中的 ValidationMessages.properties 里的错误信息（名字是固定死的）。
 * 可以通过设置 validationMessageSource 属性的值来指定配置文件。
 *
 * user.username.illegal=用户名格式不正确
 * password.length.illegal=密码[${validatedValue}]长度必须为{min}到{max}个字符
 *
 * 其中 ${validatedValue} 用来获取预校验属性的值。{min} 和 {max} 用来读取 @Size 注解中对应的属性值。
 * 你还可以像 ${max > 1 ? '大于1' : '小于等于1'} 这样使用el表达式。
 * 另外还可以拿到一个 java.util.Formatter 类型的 formatter 变量进行格式化：${formatter.format("%04d", min)}
 *
 * el jar 包：
 <dependency>
 <groupId>javax.el</groupId>
 <artifactId>javax.el-api</artifactId>
 <version>2.2.4</version>
 <scope>provided</scope>
 </dependency>
 */
public class ExceptionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInterceptor.class);

    // 或者使用拦截器 after 方法对处理错误信息进行处理后传递给页面(我们使用JSON请求的时候就需要这样做)。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if(modelAndView == null){
            return;
        }

        // 因为MappingJackson2JsonView默认会把BindingResult全部过滤掉。所以我们要想将错误消息输出，要在这里自己处理好。
        // 判断请求是否是.json、方法上是否有@ResponseBody注解，或者类上面是否有@RestController注解，有则表示为json请求
        if (!request.getRequestURI().endsWith(".json")) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            if(handlerMethod.getMethodAnnotation(ResponseBody.class) == null){
                if(handlerMethod.getBeanType().getAnnotation(RestController.class) == null){
                    return;
                }
            }
        }
        Map<String, Object> modelMap = modelAndView.getModel();
        if (modelMap != null) {
            Map<String, String> errorMsg = null;
            if(modelMap.containsKey("error")){
                errorMsg = (Map<String, String>)modelMap.get("error");
            }
            if(errorMsg == null){
                errorMsg = new HashMap<>();
                modelMap.put("error", errorMsg);
            }
            for (Map.Entry<String, Object> entry : modelMap.entrySet()) {
                if (entry.getValue() instanceof BindingResult) {
                    BindingResult bindingResult = (BindingResult) entry.getValue();
                    if (bindingResult.hasErrors()) {
                        for (FieldError fieldError : bindingResult.getFieldErrors()) {
                            errorMsg.put(fieldError.getObjectName() + "." + fieldError.getField(),
                                    fieldError.getDefaultMessage());
                        }
                    }
                }
            }
        }
    }

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
