package hyman.controller;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import hyman.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sso")
public class LoginProcessController {


    @Resource
    private IPfAccService pfAccService;

    @RequestMapping("login")
    public ModelAndView login(@RequestParam(required = true) String toUrl, @RequestParam(required = true) String token,
                              @RequestParam(required = true) String openId, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 验证token
            Boolean tokenFlag = verifyToken(Constant.getProperty("sso.verify.token", ""), openId, token);
            if (tokenFlag) {
                // Token有效
                // 根据openId获取用户信息
                Map<String, Object> queryMap = new HashMap<String, Object>();
                queryMap.put("ssoOpenid", openId);
                String userInfoStr = HttpClientUtils.httpGetRequest(Constant.getProperty("sso.userinfo.url", ""), queryMap);
                ResponseData userResultData = JSON.parseObject(userInfoStr, new TypeReference<ResponseData>() {});
                PfAcc pfAcc = JSON.parseObject(userResultData.getData().toString(), new TypeReference<PfAcc>() {});

//				Example example = new Example(PfAcc.class);
//				Criteria criteria = example.createCriteria();
//				criteria.andEqualTo("ssoOpenid", openId);
//				List<PfAcc> list = pfAccService.selectByExample(example);



//				if (CollectionUtils.isNotEmpty(list)) {
//					PfAcc pfAcc = list.get(0);
                request.getSession().setAttribute("loginUser", pfAcc);
                Boolean loginFlag = loginResult(pfAcc, request);
                if (loginFlag) { //登录成功
                    request.getSession().setAttribute("CMEPLAZAUSERID", pfAcc.getId());
                    Cookie loginCookie = new Cookie("CMEPLAZAUSERID", pfAcc.getId());
                    //登录成功获取当前登录者身份id
                    if(StringUtils.isNotBlank(pfAcc.getId())) {
                        //调取接口获取当前用户的身份id标识
                        String result=HttpClientUtils.httpPostRequest(Constant.getProperty("get_user_identity", "")+"?userId="+pfAcc.getId());
                        if(StringUtils.isNotBlank(result)) {
                            ResponseData identityData = JSON.parseObject(result, new TypeReference<ResponseData>() {});
                            if (identityData.getState()==1) {
                                String identityId=JSON.parseObject(identityData.getData().toString()).get("id").toString();
                                SecurityUtils.getSubject().getSession().setAttribute("identityId", identityId);
                            }
                        }
                    }
                    String url = "";
                    if (Constant.PROXY_IP_NAME.isEmpty()) {
                        url = request.getServerName();
                    } else {
                        url = request.getHeader("host");
                    }

                    String cookieDomain = url;
                    if (!Constant.PROXY_IP_NAME.isEmpty()) {
                        String[] urlArray = url.split("\\.");
                        cookieDomain = url.substring(urlArray[0].length());
                    }
                    loginCookie.setDomain(cookieDomain);
                    loginCookie.setPath("/");
                    loginCookie.setMaxAge(3 * 60 * 60);
                    response.addCookie(loginCookie);

                    if (StringUtils.isBlank(toUrl) || toUrl.indexOf("login") >= 0) {
                        String rootPath = Constant.getProperty("root.path", "");
                        String successUrl = Constant.getProperty("sso.successUrl", "");
                        // toUrl = "/index";
                        toUrl = rootPath + successUrl;
                    }

                    return new ModelAndView(new RedirectView(toUrl));
                } else {
                    // 没有用户信息
                    return new ModelAndView("redirect:/sso/error?errorCode=40002");
                }
//				}
            } else {
                // Token无效
                return new ModelAndView("redirect:/sso/error?errorCode=40003");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/sso/error?errorCode=40004");
    }

    /**
     * 验证有效性
     */
    private Boolean verifyToken(String url, String openId, String token) {
        try {
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("openId", openId);
            paramsMap.put("token", token);
            String resultJson = HttpClientUtils.httpGetRequest(url, paramsMap);
            JSONObject jsonObj = JSONObject.parseObject(resultJson);
            if (jsonObj.getIntValue("state") != 1) { // 无效
                return false;
            }
        } catch (Exception e) {
            // LOGGER.error("=====验证token异常====");
            return false;
        }
        return true;
    }
    private Boolean loginResult(PfAcc pfAcc, HttpServletRequest request) {
        ResponseData result = directLogin(pfAcc, request);
        if (null != result && ResponseState.SUCESS_STATE.getValue().equals(result.getState())) {
            return true;
        }
        return false;
    }
    public ResponseData directLogin(PfAcc pfAcc, HttpServletRequest request) {
        ResponseData result = new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        try {
            if (pfAcc != null && StringUtils.isNoneBlank(pfAcc.getUsername(),pfAcc.getPassword())) {
                DefaultUsernamepasswordToken token = new DefaultUsernamepasswordToken(pfAcc.getUsername(), pfAcc.getPassword(),
                        false, request.getRemoteHost());
                token.setLoginType("plat");
                SecurityUtils.getSubject().login(token);
                PfAcc user = null;
                SecurityUtils.getSubject().getSession().setAttribute("loginUser", user);
                if (pfAcc != null) {
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

    @RequestMapping(value = "error")
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("error");
        String errorCode = request.getParameter("errorCode");
        String errorMsg = request.getParameter("errorMsg");
        switch (errorCode) {
            case "40001":
                errorMsg = "登录失败";
                break;
            case "40002":
                errorMsg = "没有用户信息";
                break;
            case "40003":
                errorMsg = "Token无效";
                break;
            case "40004":
                errorMsg = "服务异常";
                break;
            case "40005":
                errorMsg = "中机用户信息接口无效";
                break;
            case "40006":
                errorMsg = "中机用户信息保存本地ID冲突";
                break;
            // case "40007":
            // errorMsg = "您无权登录该平台";
            // break;

            default:
                errorMsg = "错误";
                break;
        }
        view.addObject("errorCode", errorCode);
        view.addObject("errorMsg", errorMsg);
        PageTitle page = new PageTitle();
        loadInit(view, page);

        return view;
    }
}
