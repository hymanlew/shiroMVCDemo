package hyman.security;

import com.alibaba.fastjson.JSONObject;
import hyman.entity.User;
import hyman.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AuthenticateFilter implements Filter {

    public static final String REDIS_INTERFACE_KEY_PARAM = "cmsInterfaceKeyParam";
    private String loginUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String cookieDomain = "";
//		System.out.println(((HttpServletRequest) request).getRequestURI());
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();// 这样便可以获取一个cookie数组
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.isNotBlank(cookie.getName())) {
                    if ("CMEPLAZAUSERID".equals(cookie.getName())) {
                        cookieDomain = cookie.getValue();
                        break;
                    }
                }
            }
        }

        String sessionCmeplazaUserId = "";
        if (((HttpServletRequest) request).getSession().getAttribute("CMEPLAZAUSERID") != null) {
            sessionCmeplazaUserId = ((HttpServletRequest) request).getSession().getAttribute("CMEPLAZAUSERID")
                    .toString();
        }
        // 如果服务端退出， 则先清除本地缓存
        if (StringUtils.isBlank(cookieDomain) || !sessionCmeplazaUserId.equals(cookieDomain)) {
            // 本地登出
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.getSession().removeAttribute("loginUser");
            currentUser.logout();
        }

        if (SecurityUtils.getSubject().isAuthenticated()) { // 验证通过
            filterChain.doFilter(request, response);
        } else {
            // String servicesUrl = Constant.getProperty("sso.login.callback",
            // "");
            boolean flag = true;
            String interfaceKey = request.getParameter(REDIS_INTERFACE_KEY_PARAM);
            String interfaceValue = RedisUtil.get(interfaceKey);
            if (StringUtils.isNotBlank(interfaceKey)) {
                String checkUrl = Constant.getProperty("anon.interface.check.url", "");
                Map<String, Object> params = new HashMap<String,Object>();
                params.put("interfaceKey", interfaceKey);
                params.put("interfaceValue", interfaceValue);
                //String json = HttpClientUtils.post(checkUrl, params, 5000);
                String json = "";
                JSONObject jsonObject = JSONObject.parseObject(json);
                if (jsonObject!=null&&jsonObject.getInteger("state") == 1) {
                    chain.doFilter(request, response);
                    flag = false;
                }
            }
            if (flag){
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                Object obj = httpRequest.getSession().getAttribute("cmePlatform");
                String url = "";
                String rootPath = Constant.getProperty("root.path", "");
                if (null == obj) {
                    if (Constant.PROXY_IP_NAME.isEmpty()) {
                        url = httpRequest.getServerName();
                    } else {
                        url = httpRequest.getHeader("host");
                    }
                } else {
                    url = obj.toString();
                }
//				System.out.println(httpRequest.getRequestURL());
                String toUrl = httpRequest.getRequestURI();
                String param = httpRequest.getQueryString();
                if (StringUtils.isNotBlank(param)) {
                    toUrl = toUrl + "?" + param;
                }
                String serverPort = Constant.getProperty("server.port", "");
                String servicesUrl = "http://" + url + ((StringUtils.isBlank(serverPort) ? "" : (":" + serverPort)))
                        + rootPath + Constant.getProperty("sso.login.callback", "");
                servicesUrl += "?toUrl=" + URLEncoder.encode(toUrl, "UTF-8");
                ((HttpServletResponse) response)
                        .sendRedirect(loginUrl + "?servers=" + URLEncoder.encode(servicesUrl, "UTF-8"));
            }

        }
    }

    @Override
    public void destroy() {

    }

    public static Principal getPrincipal() {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return null;
        }
        Principal principal = (Principal) obj;
        return principal;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * 登录结果
     * @return
     */
    @SuppressWarnings("unused")
    private Boolean loginResult(BSysUser bSysUser, HttpServletRequest request) {
        ResponseData result = directLogin(bSysUser, request);
        if (null != result && ResponseState.SUCESS_STATE.getValue().equals(result.getState())) {
            return true;
        }
        return false;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b><b>后台直接登录方法</b>
     * </p>
     *
     * @param username
     *            用户帐号
     * @param password
     *            用户密码
     * @return ResponseData
     */
    public ResponseData directLogin(User bSysUser, HttpServletRequest request) {
        ResponseData result = new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        try {
            if (bSysUser != null && StringUtils.isNoneBlank(bSysUser.getUserName(),bSysUser.getPassword())) {
                DefaultUsernamepasswordToken token = new DefaultUsernamepasswordToken(bSysUser.getUserName(), bSysUser.getPassword(),
                        false, request.getRemoteHost());
                token.setLoginType("plat");
                SecurityUtils.getSubject().login(token);
                SecurityUtils.getSubject().getSession().setAttribute("loginUser", bSysUser);
                if (bSysUser != null) {
                    result = new ResponseData(ResponseData.ResponseState.SUCESS_STATE);
                    result.setMessage("自动登录成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        }
        return result;
    }
}
