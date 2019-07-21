package hyman.security;

import hyman.entity.ResponseData;
import hyman.entity.User;
import hyman.utils.CookieUtil;
import hyman.utils.CryptograpUtil;
import hyman.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticateFilter implements Filter {

    @Resource
    private PropertyUtils propertyUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateFilter.class);
    private String loginUrl;
    private String logoutUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        // 获取 sso 认证的主机地址.
        String ssoUrl = "";
        String token = "";
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
		System.out.println(request.getRequestURI());

        // 因为在本项目中是在本项目直接认证，所以就不需要远程调用 sso 验证了。
        Cookie ssocookie = CookieUtil.getCookie(request, "SSOSERVICEURL");
        if(null != ssocookie) {
            ssoUrl = ssocookie.getValue();
        }
        Cookie tokencookie = CookieUtil.getCookie(request, "token");
        if(null != tokencookie) {
            token = tokencookie.getValue();
        }
        ssoUrl = propertyUtils.getPropertiesString("sso.service.url");

        // 如果服务端退出， 则先清除本地缓存再退出
        if (StringUtils.isBlank(ssoUrl) || !verifyToken(token, user)) {
            response.sendRedirect(logoutUrl);
            return;
        }
        // 如果验证通过就放行
        if (SecurityUtils.getSubject().isAuthenticated()) {
            //令牌组成：sso密钥+用户id，进行md5加密
            String uid = user.getId().toString();
            String ssotoken = propertyUtils.getPropertiesString("sso.verify.token").concat(uid);
            Cookie cookie = new Cookie("token", CryptograpUtil.md5(ssotoken, "hyman"));
            cookie.setPath("/");
            response.addCookie(cookie);
            filterChain.doFilter(request, servletResponse);
        } else {
            //String toUrl = request.getRequestURI();
            //String param = request.getQueryString();
            //if (StringUtils.isNotBlank(param)) {
            //    toUrl = toUrl + "?" + param;
            //}
            //String serverPort = Constant.getProperty("server.port", "");
            //String servicesUrl = "http://" + url + ((StringUtils.isBlank(serverPort) ? "" : (":" + serverPort)))
            //        + rootPath + Constant.getProperty("sso.login.callback", "");
            //servicesUrl += "?toUrl=" + URLEncoder.encode(toUrl, "UTF-8");
            //response.sendRedirect(loginUrl + "?servers=" + URLEncoder.encode(servicesUrl, "UTF-8"));

            response.sendRedirect(logoutUrl);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 验证有效性
     */
    private Boolean verifyToken(String token, User user) {
        if(StringUtils.isBlank(token) || null == user){
            return false;
        }
        String uid = user.getId().toString();
        String systoken = propertyUtils.getPropertiesString("sso.verify.token").concat(uid);
        if (!CryptograpUtil.md5(systoken, "hyman").equals(token)) {
            LOGGER.error("===== token 无效====");
            return false;
        }
        return true;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }


    /**
     * <p>
     * <b>方法描述：</b><b>后台直接登录方法</b>
     * </p>
     */
    public ResponseData directLogin(User bSysUser, HttpServletRequest request) {
        ResponseData result = new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        try {
            if (bSysUser != null && StringUtils.isNoneBlank(bSysUser.getName(),bSysUser.getPassword())) {
                UsernamePasswordToken token = new UsernamePasswordToken(bSysUser.getName(), bSysUser.getPassword(),
                        false, request.getRemoteHost());
                SecurityUtils.getSubject().login(token);
                SecurityUtils.getSubject().getSession().setAttribute("loginUser", bSysUser.getId());
                if (bSysUser != null) {
                    result = new ResponseData(ResponseData.ResponseState.SUCESS_STATE);
                    result.setMsg("自动登录成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        }
        return result;
    }
}
