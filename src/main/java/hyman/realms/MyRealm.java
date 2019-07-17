package hyman.realms;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import hyman.entity.Permission;
import hyman.entity.ResponseData;
import hyman.entity.Roles;
import hyman.entity.User;
import hyman.security.PermissionService;
import hyman.service.UserService;
import hyman.utils.Constant;
import hyman.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class MyRealm extends AuthorizingRealm{

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRealm.class);

    @Resource(name = "service")
    private UserService userService;

    @Override
    public String getName() {
        // 这个名字可以随便取
        return "myrealm";
    }

    /**
     * 为当前认证通过的用户提取其对应的角色及权限进行封装
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {

        if (principal == null) {
            throw new AuthorizationException("没有认证实体类！");
        }
        // 接收已经认证通过的用户信息：用户名，密码
        String username = (String)principal.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 设置角色，权限
        List<Roles> roles = userService.getRolesByName(username);
        Set set = new LinkedHashSet();
        List<Integer> ids = new ArrayList<>();
        for(Roles roles1:roles){
            set.add(roles1.getName());
            ids.add(roles1.getId());
        }
        info.setRoles(set);

        // 直接查询数据库获取
        //set = new LinkedHashSet();
        //List<Permission> permissionList = userService.getPermis(ids);
        //for(Permission permission : permissionList){
        //    set.add(permission.getName());
        //}
        //info.setStringPermissions(set);

        // 封装数据库查询的方法，再进行获取
        info.setStringPermissions(findPermissions(ids));
        return info;
    }

    /**
     * 为当前登录的用户进行身份认证封装
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 在spring mvc中，为了随时都能取到当前请求的 request 对象，可以通过 RequestContextHolder 的静态方法 getRequestAttributes()
        // 获取 Request 相关的变量，如 request, response 等。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object userObj = request.getSession().getAttribute("loginUser");
        if (userObj == null) {
            throw new UnknownAccountException("代码错误异常 ==== 只能在需要远程 sso 验证时才使用此代码 ====");
        }

        // 接收来自 subject 的用户信息：用户名，密码
        // 需要添加密码 hash 加密验证
        String username = (String)token.getPrincipal();
        User baseUser = userService.getByname(username);
        if(baseUser!=null){
            // 封装认证信息，其内部会自动比对密码是否一致
            AuthenticationInfo info = new SimpleAuthenticationInfo(baseUser.getName(),baseUser.getPassword(),getName());
            return info;
        }else {
            return null;
        }
    }

    // 封装的查询用户权限的实现类 service
    private List<PermissionService> permissionServices;

    @ExceptionHandler
    private Set<String> findPermissions(List<Integer> ids) {

        // 在spring mvc中，为了随时都能取到当前请求的 request 对象，可以通过 RequestContextHolder 的静态方法 getRequestAttributes()
        // 获取 Request 相关的变量，如 request, response 等。
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        Set<String> permissions = new HashSet<String>();
        if (permissionServices == null) {
            return permissions;
        }
        for (PermissionService permissionService : permissionServices) {
            permissions.addAll(permissionService.findPermissions(ids));
        }
        return permissions;
    }

    // 单点登录识别参数，认证密码匹配调用方法
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
            throws AuthenticationException {

        try {
            // 如果有特殊的密码或是属性验证需要，则可以自定义验证流程。如果没有就可以不需要这些代码，只执行默认的验证方法即可
            // （但是我们也自定义了 HashedCredentialsMatcher 代替 shiro 默认的）。
            if (token instanceof UsernamePasswordToken) {
                if (StringUtils.isNotBlank(((UsernamePasswordToken) token).getUsername())) {

                    String vc = (String) SecurityUtils.getSubject().getSession()
                            .getAttribute("login" + token.getPrincipal().toString());

                    char[] pwd = ((UsernamePasswordToken) token).getPassword();
                    String pwdStr = new String(pwd);

                    //if (StringUtils.isBlank(vc) || !vc.equals(pwdStr)) {
                    //    String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
                    //    throw new IncorrectCredentialsException(msg);
                    //} else {
                    //    SecurityUtils.getSubject().getSession().removeAttribute("login" + token.getPrincipal().toString());
                    //}
                    super.assertCredentialsMatch(token, info);

                } else {
                    super.assertCredentialsMatch(token, info);
                }
            } else {
                super.assertCredentialsMatch(token, info);
            }
        } catch (IncorrectCredentialsException e) {
            LOGGER.debug(e.getMessage());
            throw new IncorrectCredentialsException(e.getMessage());
        }
    }

    // 清除 ehcache 缓存
    public void clearUserCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }

    public List<PermissionService> getPermissionServices() {
        return permissionServices;
    }

    public void setPermissionServices(List<PermissionService> permissionServices) {
        this.permissionServices = permissionServices;
    }
}
