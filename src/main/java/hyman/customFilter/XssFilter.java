package hyman.customFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// 正式创建过滤器，使用我们定义的 HttpServletRequestWrapper的子类 包装类去替换掉它原来的 HttpServletRequest.
// xss 过滤
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response); // 这里使用的是HttpServletRequest的子类
    }

    @Override
    public void destroy() {

    }
}
