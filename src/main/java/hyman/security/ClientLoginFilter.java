package hyman.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hyman.entity.ResponseData;
import hyman.entity.User;
import hyman.service.UserService;
import hyman.utils.Constant;
import hyman.utils.EncryptUtils;
import hyman.utils.HttpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 客户端登录验证过滤器
public class ClientLoginFilter implements Filter {

    private static final String COOKIE_OPEN_ID = "CMEPLAZAOPENID";

    /** 密钥 */
    private static final String AES_KEY = "CMEPLATF_AES_KEY";

    /** 用户信息 */
    @Resource(name = "service")
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String resultJson = "";

        String clientToken = request.getParameter("clientToken");
        String clienType = request.getParameter("clientType");
        String ssoOpenId = request.getParameter("ssoOpenId");

        Map<String, String> params = new HashMap<String, String>();
        params.put("clientToken", clientToken);
        params.put("token", clientToken);
        params.put("openId", ssoOpenId);
        if ("pc".equals(clienType)){
            resultJson = HttpUtil.getPostData(Constant.getProperty("pcclient.token.url", ""), params);
        } else {
            if (!"self".equals(clienType)){
                resultJson = HttpUtil.getPostData(Constant.getProperty("client.token.url", ""), params);
            }
        }

        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null && currentUser.isAuthenticated()){
            filterChain.doFilter(request, response);
            return;
        }

        //客户端token为空
        if (StringUtils.isBlank(clientToken)){
            filterChain.doFilter(request, response);
            return;
        }

        //不是同一浏览器打开 需要验证openId是否为空
        if (!"self".equals(clienType)){
            if (StringUtils.isBlank(ssoOpenId)){
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 获取参数
        String toUrl = request.getRequestURI();
        String queryStr = request.getQueryString();
        if (StringUtils.isNoneBlank(queryStr)){
            toUrl += "?" + queryStr;
        }

        if ("self".equals(clienType)){
            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
            if (cookies != null){
                for(Cookie cookie : cookies){
                    if (StringUtils.isNotBlank(cookie.getName())&&(COOKIE_OPEN_ID + "_" + clientToken).equals(cookie.getName())) {
                        ResponseData data = new ResponseData(ResponseData.ResponseState.SUCESS_STATE);
                        data.setMsg("获取cookie成功");
                        Map<String, Object> dataMap = new HashMap<String, Object>();
                        dataMap.put("ssoOpenId", cookie.getValue());
                        data.setData(dataMap);
                        resultJson = JSONObject.toJSONString(data);
                        break;
                    }
                }
            }
        }

        if (StringUtils.isBlank(resultJson)){
            //logger.error("===客户端请求地址错误,返回数据为空===");
            filterChain.doFilter(request, response);
            return;
        }

        try{
            JSONObject jsonObj = JSON.parseObject(resultJson);
            //返回数据未成功
            if (!"1".equals(jsonObj.getString("state"))){
                filterChain.doFilter(request, response);
                return;
            }

            //1、获取地址
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            Object obj = httpRequest.getSession().getAttribute("cmePlatform");
            String url = "";
            String rootPath = Constant.getProperty("root.path", "");
            if (null == obj) {
                if (Constant.PROXY_IP_NAME.isEmpty()) {
                    url =  httpRequest.getServerName();
                } else {
                    url =  httpRequest.getHeader("host");
                }
            }else{
                //url = obj.toString();
            }

            // 并把地址参数带到sso
            JSONObject dataJson = jsonObj.getJSONObject("data");
            if ("self".equals(clienType)){
                ssoOpenId = dataJson.getString("ssoOpenId");
            }
            //根据openId获取用户信息
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
//			criteria.andEqualTo("ssoOpenId", "ff8080815ab307fc015abacfc90a0822");
            criteria.andEqualTo("ssoOpenId", ssoOpenId);
            //List<User> list = userService.selectByExample(example);
            List<User> list = null;

            //判断是否有用户信息
            if (CollectionUtils.isNotEmpty(list)) {

                String ttoken = EncryptUtils.encryptToAES(AES_KEY, System.currentTimeMillis()+"");
                String clientOpenId = EncryptUtils.encryptToAES(AES_KEY, ssoOpenId);
                //4、跳转sso(有用户信息)
                String serverPort = Constant.getProperty("server.port", "");
                String servicesUrl ="http://"+url+((StringUtils.isBlank(serverPort) ? "":(":" + serverPort)))+rootPath+ Constant.getProperty("sso.login.callback", "");
                servicesUrl += "?toUrl=" + URLEncoder.encode(toUrl, "UTF-8");
                ((HttpServletResponse)response).sendRedirect(Constant.getProperty("sso.directloginUrl", "")+"?servers="+URLEncoder.encode(servicesUrl, "UTF-8") + "&ttoken=" + ttoken + "&clientOpenId=" + clientOpenId);

            }else{
                filterChain.doFilter(request, response);
                return;
            }
        } catch(Exception e){
            //logger.error("===验证客户端token返回数据非json格式===");
            filterChain.doFilter(request, response);
            return;
        }
    }

    @Override
    public void destroy() {

    }
}
