package hyman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @RequestMapping("/admin")
    public void admin(){
        System.out.println("===== admin ======");
    }

    @RequestMapping("/teach")
    public void teach(){
        System.out.println("===== teach ======");
    }

    @RequestMapping("/stud")
    public void stud(){
        System.out.println("===== stud ======");
    }

    /**
     * spring mvc处理方法支持如下的返回方式：ModelAndView, Model, ModelMap, Map,View, String, void
     *
     * 通过ModelAndView构造方法可以指定返回的页面名称，也可以通过setViewName()方法跳转到指定的页面 , 使用 addObject() 设置
     * 需要返回的值，addObject()有几个不同参数的方法，可以默认和指定返回对象的名字。 调用 addObject()方法将值设置到一个名为
     * ModelMap的类属性，ModelMap是LinkedHashMap的子类， 具体请看类。
     *
     * Model 是一个接口， 其实现类为ExtendedModelMap，继承了ModelMap类。
     * ModelAndView：使用它可以返回任意类型对象（测试）。
     *
     * 并且可以把对象，put 入获取的Map对象中，传到对应的视图： map.put("user",user);  return "view";
     */
    @RequestMapping("/user")
    public ModelAndView user(){
        System.out.println("===== 只要登录成功就可访问！ ======");
        ModelAndView view = new ModelAndView("DemoTest");
        return view;
    }

    /**
     * 1.使用 String 作为请求处理方法的返回值类型是比较通用的方法，这样返回的逻辑视图名不会和请求 URL 绑定，具有很大的灵活性，
     * 而模型数据又可以通过 ModelMap 控制。
     * 2.使用void,map,Model 时，返回对应的逻辑视图名称真实url为：prefix前缀+视图名称 +suffix后缀组成。
     * 3.使用String,ModelAndView返回视图名称可以不受请求的url绑定，ModelAndView可以设置返回的视图名称。
     *
     * 不能使用 ajax 方式进行访问，否则不能正常返回页面。
     */
    @RequestMapping("/model")
    public ModelAndView model(){
        System.out.println("===== 只要登录成功就可访问！ ======");
        //ModelAndView view = new ModelAndView("forward:DemoTest"); 会转发到对应到 controller 路径
        //ModelAndView view = new ModelAndView("redirect:/WEB-INF/jsp/DemoTest.jsp"); 会重定向失败
        ModelAndView view = new ModelAndView("DemoTest");
        return view;
    }

    /**
     * 注解式授权（事先也已经在）：
     *
     * @RequiresAuthentication： 要求当前 subject 已经在当前的 session中被验证通过才能被访问和调用。
     *              即：SecurityUtils.getSubject().isAuthenticated()。
     *
     * @RequiresGuest： 要求当前的 subject是一个‘guest’，即他们必须是在之前的 session中没有被验证或被记住才能被访问或调用。
     *              与@ RequiresUser完全相反。即 RequiresUser  == ! RequiresGuest，Subject().getPrincipals() = null。
     *
     * @RequiresPermissions("user:select")： 要求当前的 subject拥有相关权限才能访问。否则抛出异常 AuthorizationException。
     *              一个权限（user:select）,多个权限（value = {"11","22"}）。
     *
     * @RequiresRoles("hyman") 要求当前登录的主体拥有相关的角色才能访问。如果没有，则会抛 AuthorizationException 异常。
     *              多个角色（value = {"hyman","test"}）。
     *
     * @RequiresUser： 验证用户是否被记忆，user有两种含义：
     *      一种是成功登录的（subject.isAuthenticated() 结果为true）；
     *      另外一种是被记忆的（subject.isRemembered() 结果为true）。
     *
     */
}
