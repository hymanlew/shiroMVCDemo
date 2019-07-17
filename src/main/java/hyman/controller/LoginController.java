package hyman.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hyman.entity.User;
import hyman.realms.MyRealm;
import hyman.service.UserService;
import hyman.utils.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/")
public class LoginController {

    @Resource
    private MyRealm myRealm;
    @Resource(name = "service")
    private UserService userService;
    @Resource
    private PropertyUtils propertyUtils;

    @RequestMapping({"/","/index"})
    public String toIndex(){
        return "redirect:/index.jsp";
    }

    /**
     * 如果本项目只是一个业务系统，而认证是有单独的 sso 登录系统。那么本项目中又由于 shrio 权限要做登录校验，而登录认证已交给
     * sso处理，那么这里只需要保证 shiro 的前后校验能通过就行，把 password 改成常量，并取消原来的加盐。
     * 即前端登录时密码被换成系统中的常量，而在 realm 中也使用该常量作为密码，则就会直接通过验证。或者直接在此系统中不设置登录，
     * 退出操作，全部是业务访问，且让 shiroFilter 拦截所有访问路径。
     *
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest req, HttpServletResponse response, User user) {
        System.out.println("Login doPost！");

        // 默认是根据 web.xml 中的 shiro 配置文件，来得到认证主体。
        Session session = SecurityUtils.getSubject().getSession();
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            return "success";
        }else {
            System.out.println("========== 没有登录认证过！要走 login！==========");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), CryptograpUtil.md5(user.getPassword(),"hyman"));
        try {
            // 记住我功能，默认有效期为一周。不建议使用这个（即 shiro框架自带的），要自己封装 cookie（用原始 js）。
            if(subject.isRemembered()){
                System.out.println("已经记住当前用户！");
            }else {
                token.setRememberMe(true);
            }

            // login 方法会调用 realm 进行用户验证，验证成功后即可返回成功页面。但不会去访问具体有什么权限，只有在页面中
            // 有权限的需求时才会去查询权限（即 success 页面中某种权限的显示）。
            System.out.println(token.getPassword());
            subject.login(token);

            // 存入 redis，session，cookie
            //将用户信息存入session，供本地存储用户cookie使用，注意该session不是项目内的session
            req.getSession().setAttribute("loginUser", user);

            //将用户信息存入session
            SecurityUtils.getSubject().getSession().setAttribute("loginUser", user);

            session.setAttribute("loginUser",user.getId());
            String ssotoken = propertyUtils.getPropertiesString("sso.verify.token").concat(user.getId().toString());
            CookieUtil.setCookie(response,"token", CryptograpUtil.md5(ssotoken, "hyman"));
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            session.setAttribute("error","用户信息错误！");
            // 转发
            return "redirect:/login.jsp";
        }
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest req, HttpServletResponse response, User user) throws ServletException, IOException {

        // 直接调用 Subject.logout 即可。
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().removeAttribute("loginUser");
        subject.logout();

        /**
         * shiro的 SecurityUtils.getSubject().getSession() 和 从 request中获取的session：
         *
         * shiro 在 shiro过滤器中的 doFilterInternal 方法对 ServletRequest 和 ServletReponse 进行了包装。并把包装后的 request/response，
         * 和 principals, session, securityManager 都作为参数创建 Subject，这个 subject 其实是代理类 DelegatingSubject。
         * 即 request 对象是 org.apache.shiro.web.servlet.ShiroHttpServletRequest，很明显此 request 已经被shiro包装过了。
         *
         * 在 controller中，通过 request.getSession() 获取的会话 session，到底来源 servletRequest 还是由 shiro管理并管理创建的会话，
         * 主要由安全管理器 SecurityManager 和 SessionManager 会话管理器决定（即是否有配置 SessionManager）。
         * 但不管是通过 request.getSession 或者 subject.getSession 获取到的session，在操作 session 时两者都是等价的。在使用默认
         * session 管理器的情况下，操作 session 都是等价于操作 HttpSession。
         */

        // 清空 ehcache 中当前用户的缓存信息
        myRealm.clearUserCache();
        // 清空 redis 中当前用户的缓存信息
        RedisUtil.del("loginUser");
        // 清除 token cookie
        CookieUtil.removeCookie(req, response, "token");

        // 两种重定向的方式，并传递参数列表
        //return "redirect:/login.jsp";
        //return new ModelAndView("/login.jsp");
        return new ModelAndView(new RedirectView("/login.jsp"), new HashMap<>());
    }

    public void setMyRealm(MyRealm myRealm) {
        User user = JSON.parseObject("", new TypeReference<User>() {});
    }
}
