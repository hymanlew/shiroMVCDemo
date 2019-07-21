package hyman.controller;

import hyman.config.freemarker.FreeMarkers;
import hyman.entity.User;
import hyman.service.ValidatorService;
import hyman.utils.Logutil;
import hyman.utils.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {

    @Resource(name = "freemarkerConfig")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping("/index")
    public String dopage(HttpServletRequest request) throws Exception{

        Map<String, Object> map = new HashMap<>();
        String  html = "";
        html = FreeMarkers.renderString("/productinfo/list2.htm", map, freeMarkerConfigurer.createConfiguration());
        html = getHtml(request, html);
        return html;
    }

    /**
     * 需要注意在方法中使用验证时，@Valid 的参数后必须紧挨着一个BindingResult 参数，否则spring会在校验不通过时直接抛出异常。
     */
    @PostMapping("/addUser")
    public String addEmp(Map<String,Object> map, @Validated User user, BindingResult result){
        Logutil.logger.info("=== 添加工人："+user.toString());

        if(result.hasErrors()){
            Logutil.getValidData(map, result);
            return "error.ftl";
        }else {
            // 在这里必须使用 redirect 重定向，因为两个方法的请求方式不同（GET，POST）。如果是同一种请求方式，则应该使用 forward。
            //return "forward:/emp/emps";
            return "redirect:sucess.jsp";
        }
    }

    /**
     * // 除了 @ModelAttribute 绑定参数之外，还可以通过 @RequestParam从页面中接收传递的参数。
     * @ModelAttribute 适用于接收一组参数变量。
     * @param result
     * @param model
     * @return
     */
    @RequestMapping("/addUser2")
    public String addEmp(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors()) {
            return "validator.ftl";
        }
        return "redirect:sucess.jsp";
    }

    @RequestMapping("/addUser3")
    public String addEmp3(@Valid User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors()) {
            return "validator.ftl";
        }
        return "redirect:sucess.jsp";
    }

    private String getHtml(HttpServletRequest request, String html) {

        String contentId = request.getParameter("contentId");
        String contentType = request.getParameter("contentType");
        String projectId = request.getParameter("projectId");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("contentId", contentId);
        paramMap.put("userId", UserUtils.getCurrentUserId());
        paramMap.put("contentType", contentType);

        String pageHtml = "";
        pageHtml = FreeMarkers.renderString("/struct/struct.htm", getSysStaticParamsMap(),
                freeMarkerConfigurer.getConfiguration());
        pageHtml = pageHtml.replace("${html!}", html);
        return pageHtml;
    }

    private Map<String, Object> getSysStaticParamsMap(){

        // 存储系统中的常用变量，例如 basepath，filepath，staticpath 等等。
        Map<String, Object> map = new HashMap<>();
        return map;
    }

    /**
     * SpringMVC 使用验证框架 Bean Validation：
     * 对于任何一个应用而言在客户端做的数据有效性验证都不是安全有效的，这时候就要求我们在开发的时候在服务端也对数据的有效性进行验证。
     *
     * SpringMVC 自身对数据在服务端的校验(Hibernate Validator)有一个比较好的支持，它能将我们提交到服务端的数据按照我们事先的约定
     * 进行数据有效性验证，对于不合格的数据信息 SpringMVC 会把它保存在错误对象中(Errors接口的子类)，这些错误信息我们也可以通过
     * SpringMVC 提供的标签(form:errors)在前端JSP页面上进行展示（必须放在 form:form 标签中使用）。
     * 或者使用 map 进行封装然后在页面中直接接收显示。
     *
     * 详细信息在 hyman.aop.group 类中。
     *
     */

    @Resource
    private ValidatorService validatorService;

     // 测试方法级别的验证（如果验证失败，则会抛出异常 ConstraintViolationException）
    @RequestMapping("/valiMethod")
    @ResponseBody
    public Model valiMethod(String name, String password, Model model){

        try {
            String content = validatorService.getContent(name, password);
            model.addAttribute("name", content);
        } catch (ConstraintViolationException e) {
            addErrorMessage(model, e);
        }
        return model;
    }

    /**
     * 添加错误消息，建议将该方法提取为一个公共的方法使用。
     */
    protected void addErrorMessage(Model model, ConstraintViolationException e){
        Map<String, String> errorMsg = new HashMap<>();
        model.addAttribute("errorMsg", errorMsg);

        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {

            // 获得验证失败的类 constraintViolation.getLeafBean()
            // 获得验证失败的值 constraintViolation.getInvalidValue()
            // 获取参数值 constraintViolation.getExecutableParameters()
            // 获得返回值 constraintViolation.getExecutableReturnValue()
            errorMsg.put(constraintViolation.getLeafBean().getClass().getName() + "-" + constraintViolation.getPropertyPath
                    ().toString(), constraintViolation.getMessage());
        }
    }

}
