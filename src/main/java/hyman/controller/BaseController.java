package hyman.controller;

import hyman.config.CustomException;
import hyman.config.freemarker.FreeMarkers;
import hyman.entity.ResponseData;
import hyman.utils.BeanValidators;
import hyman.utils.Constant;
import hyman.utils.HttpUtil;
import hyman.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController {

    /***
     * 服务总线服务器地址
     */
    @Value("${esbServerPath}")
    protected String esbServerPath;


    /***
     * 服务总线工作办理接口地址
     */
    @Value("${flowSchedule}")
    protected String flowSchedule;


    /***
     * 国际化资源
     */
    @Resource
    private MessageSourceAccessor messageSourceAccessor;

    /**
     * 模板配置对象
     */
    @Resource(name = "freemarkerConfig")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @ModelAttribute
    public void setAttribute(HttpServletRequest request,HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>操作成功默认提示
     * </p>
     *
     * @return 输出信息的对象
     */
    protected ResponseData operateSucess() {
        return operateSucess(messageSourceAccessor.getMessage("message.operation.sucess"));
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>自定义操作成功提示信息
     * </p>
     *
     * @param message
     *            成功时的信息
     * @return 输出信息的对象
     */
    protected ResponseData operateSucess(String message) {
        ResponseData result = new ResponseData(ResponseData.ResponseState.SUCESS_STATE);
        result.setMsg(message);
        return result;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>自定义操作成功提示信息，并返回数据
     * </p>
     *
     * @param message
     *            成功时的信息
     * @param data
     *            数据对象
     * @return 输出信息的对象
     */
    protected ResponseData operateSucess(String message, Object data) {
        ResponseData result = operateSucess(message);
        result.setData(data);
        return result;
    }

    /***
     * <p><b>方法描述：</b>自定义操作成功提示信息，并返回数据和code</p>
     * @param message 成功时的信息
     * @param data 数据对象
     * @param code code编码
     * @return ResponseData
     */
    protected ResponseData operateSucess(String message, Object data, String code) {
        ResponseData result = operateSucess(message, data);
        result.setState(code);
        return result;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>自定义失败提示信息
     * </p>
     *
     * @param message 失败时的信息
     * @return 输出信息的对象
     */
    protected ResponseData operateFailed(String message) {
        ResponseData result = new ResponseData(ResponseData.ResponseState.FAILED_STATE);
        result.setMsg(message);
        return result;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>自定义失败提示信息并返回数据
     * </p>
     *
     * @param message 失败时的信息
     * @param data 数据对象
     * @return 输出信息的对象
     */
    protected ResponseData operateFailed(String message, Object data) {
        ResponseData result = operateFailed(message);
        result.setData(data);
        return result;
    }

    /***
     * <p><b>方法描述：</b>自定义失败提示信息并返回数据和code</p>
     * @param message 失败时的信息
     * @param data 数据对象
     * @param code code编码
     * @return ResponseData
     */
    protected ResponseData operateFailed(String message, Object data, String code) {
        ResponseData result = operateFailed(message, data);
        result.setState(code);
        return result;
    }

    /**
     *
     * <p>
     * <b>方法描述：</b>获取@Valid校验，返回的全部错误信息
     * </p>
     *
     * @param result
     *            校验信息对象
     * @return 返回全部错误信息
     */
    protected Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<String, String>();
        List<FieldError> list = result.getFieldErrors();
        for (FieldError error : list) {
            map.put(error.getField(), error.getDefaultMessage());
        }
        return map;
    }

    /***
     *
     * <p>
     * <b>方法描述：</b>防止页面重复提交,页面提交成功生，清除token
     * </p>
     *
     * @param request
     *            request请求
     */
    protected void tokenRemove(HttpServletRequest request) {
        request.getSession(false).removeAttribute("token");
    }

    public MessageSourceAccessor getMessageSourceAccessor() {
        return messageSourceAccessor;
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否有错</p>
     * @param result 校验信息对象
     */
    protected void validation(BindingResult result) {
        if (result.hasErrors()) {
            throw new CustomException(result.getFieldError().getDefaultMessage());
        }
    }

    /***
     *
     * <p>
     * <b>方法描述：</b>获取ＩＰ地址
     * </p>
     * @param request request请求
     * @return ＩＰ地址
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = "";
        if (Constant.PROXY_IP_NAME.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader(Constant.PROXY_IP_NAME);
        }
        return ip;
    }

    public ResponseData directLogin(String username, String password) {
        ResponseData result;
        try {
            result = new ResponseData(ResponseData.ResponseState.FAILED_STATE);
            Subject currentUser = SecurityUtils.getSubject();
            if (!currentUser.isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                try {
                    currentUser.login(token);
                    result.setState(ResponseData.ResponseState.SUCESS_STATE);
                } catch (UnknownAccountException ex) {
                    throw new CustomException("账号不存在");
                } catch (IncorrectCredentialsException ex) {
                    throw new CustomException("密码错误");
                } catch (LockedAccountException ex) {
                    throw new CustomException("账号已被锁定，请与管理员联系");
                } catch (AuthenticationException ex) {
                    throw new CustomException("您没有授权");
                }
            } else {
                result = new ResponseData(ResponseData.ResponseState.SUCESS_STATE);
                result.setMsg("自动登录成功");
            }
        } catch (CustomException e) {
            return operateFailed(e.getMessage());
        }
        return result;
    }

    /**
     *
     * <p><b>方法描述：</b>写入html到response</p>
     * @param response
     * @param html
     * @throws IOException
     */
    protected void writeToResponse(HttpServletResponse response, String html) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(html);
    }

    /**
     * 获取页面html
     * <p><b>方法描述：</b>获取页面html</p>
     * @param request request对象
     * @param html 区块html
     * @return 页面html
     */
    protected String getHtml(HttpServletRequest request, String html) {
        String contentId = request.getParameter("contentId");
        String contentType = request.getParameter("contentType");
        String projectId = request.getParameter("projectId");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("contentId", contentId);
        paramMap.put("userId", UserUtils.getCurrentUserId());
        paramMap.put("contentType", contentType);

        // String pageHtml =
        // HttpClientUtils.httpGetRequest(Constant.getProperty("pageTheme.url",
        // ""), paramMap);
        // if (StringUtils.isBlank(pageHtml)) {
        // try {
        // pageHtml = FreeMarkers.renderString("/struct/struct.htm",
        // getSysStaticParamsMap(), freeMarkerConfigurer.getConfiguration());
        // } catch (IOException | TemplateException e) {
        // e.printStackTrace();
        // }
        // }

        String pageHtml = "";
        pageHtml = FreeMarkers.renderString("/struct/struct.htm", getSysStaticParamsMap(),
                freeMarkerConfigurer.getConfiguration());
        pageHtml = pageHtml.replace("${html!}", html);
        return pageHtml;
    }

    protected Map<String, Object> getSysStaticParamsMap() {
        Map<String, Object> staticParamsMap = new HashMap<String, Object>();
        staticParamsMap.put("RootPath", Constant.ROOT_PATH);
        staticParamsMap.put("FilePath", Constant.FILE_PATH);
        staticParamsMap.put("StaticPath", Constant.STATIC_PATH);
        staticParamsMap.put("AccessPath", Constant.ACCESS_PATH);
        staticParamsMap.put("UploadPath", Constant.getProperty("FilePath", "http://cir.cmeplaza.com/cme-sso-app")+"/space/upload-page.htm");
        staticParamsMap.put("DownLoadPath", Constant.DOWNLOAD_PATH);
        staticParamsMap.put("CutPath", Constant.PUT_PATH);
        staticParamsMap.put("JsPath", Constant.JS_PATH);
        staticParamsMap.put("shopCarPage", Constant.getProperty("mall.carlist.link", ""));
        staticParamsMap.put("openAuditUrl", Constant.getProperty("open_audit_url", ""));
        staticParamsMap.put("previewAuditUrl", Constant.getProperty("preview_audit_url", ""));
        staticParamsMap.put("previewReviewUrl", Constant.getProperty("preview_review_url", ""));
        staticParamsMap.put("openReviewUrl", Constant.getProperty("open_review_url", ""));
        if (null != request.getAttribute("planId")) {
            staticParamsMap.put("planId", request.getAttribute("planId"));
        }
        String wordId =	request.getParameter("workId");
        if (StringUtils.isNotBlank(wordId)) {
            staticParamsMap.put("workId", wordId);
        }

        if (null != request.getAttribute("flowId")) {
            staticParamsMap.put("flowId", request.getAttribute("flowId"));
        }
        if (null != request.getAttribute("nodeId")) {
            staticParamsMap.put("nodeId", request.getAttribute("nodeId"));
        }
        return staticParamsMap;
    }

    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中String类型转换为Date类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
	/*	binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});*/
        // Date 类型转换，选择使用，不用可以注释掉
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    //字符串转日期格式 格式：yyyy-MM-dd HH:mm:ss
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    setValue(dateFormat.parse(text));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? DateUtils.formatDateTime((Date)value) : "";
//			}
        });
    }

    /**
     * 验证Bean实例对象
     */
    @Resource(name = "validator")
    protected Validator validator;

    /**
     * 服务端参数有效性验证
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidator(Object object, Class<?>... groups) {
        try{
            BeanValidators.validateWithException(validator, object, groups);
        }catch(ConstraintViolationException ex){
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            return false;
        }
        return true;
    }
    /**
     * 工作办理接口
     * @param workId
     * @return
     */
    protected boolean sendFlowSchedule(String workId) throws Exception {

        //获取当前登陆用户id
        String userId = UserUtils.getCurrentUserId();

        if (StringUtils.isNoneBlank(userId,workId)) {

            Map<String, String> params = new HashMap<String, String>();
            params.put("userId", userId);
            params.put("workId", workId);

            String res = HttpUtil.getPostData(esbServerPath + flowSchedule, params);
            System.out.println("工作办理接口===>" + res);
        }

        return true;
    }
}
