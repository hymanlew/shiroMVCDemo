package hyman.controller;

import hyman.entity.User;
import hyman.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import hyman.utils.CryptograpUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class AuthController {

    @Resource(name = "service")
    private UserService userService;

    @RequestMapping({"/","/index"})
    public String toIndex(){
        return "redirect:/index.jsp";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest req, User user) throws ServletException, IOException {
        System.out.println("Login doPost！");

        // 默认是根据 web.xml 中的 shiro 配置文件，来得到认证主体。
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
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

            return "success";
        }catch (Exception e){
            e.printStackTrace();
            session.setAttribute("error","用户信息错误！");
            // 转发
            return "redirect:/login.jsp";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest req, User user) throws ServletException, IOException {
        // 直接调用 Subject.logout 即可。
        SecurityUtils.getSubject().logout();
        return "redirect:/login.jsp";
    }
}
