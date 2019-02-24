package hyman.realms;

import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;
import hyman.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MyRealm extends AuthorizingRealm{

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

        set = new LinkedHashSet();
        List<Permission> permissionList = userService.getPermis(ids);
        for(Permission permission : permissionList){
            set.add(permission.getName());
        }
        info.setStringPermissions(set);
        return info;
    }

    /**
     * 为当前登录的用户进行身份认证封装
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 接收来自 subject 的用户信息：用户名，密码
        String username = (String)token.getPrincipal();
        User baseUser = userService.getByname(username);
        if(baseUser!=null){
            // 封装认证信息，其内部会自动比对密码是否一致
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute("userId",baseUser.getId());
            AuthenticationInfo info = new SimpleAuthenticationInfo(baseUser.getName(),baseUser.getPassword(),getName());
            return info;
        }else {
            return null;
        }

    }
}
