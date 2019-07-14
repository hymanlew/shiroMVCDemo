package hyman.realms;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;
import hyman.service.UserService;
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

        set = new LinkedHashSet();
        List<Permission> permissionList = userService.getPermis(ids);
        for(Permission permission : permissionList){
            set.add(permission.getName());
        }
        info.setStringPermissions(set);

        info.setStringPermissions(findPermissions());
        return info;
    }

    /**
     * 为当前登录的用户进行身份认证封装
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 接收来自 subject 的用户信息：用户名，密码
        // 需要添加密码 hash 加密验证
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

    /**
     * 鏉冮檺鍒楄〃
     */
    private List<IPermissionService> permissionServices;



    @ExceptionHandler
    protected Set<String> findPermissions() {
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        PfAcc pfAcc=new PfAcc();
        BSysUser bSysUser=new BSysUser();
        Set<String> permissions = new HashSet<String>();
        if (permissionServices == null) {
            return permissions;
        }
        if ("major".equals(Constant.getProperty("app.type", "")) || StringUtils.isEmpty(Constant.getProperty("app.type", ""))) {
            if("smart".equals(Constant.getProperty("sso.type", ""))){
                //鏅鸿兘浜烘煡璇㈡暟鎹潈闄�
                pfAcc = (PfAcc) UserUtils.getCurrentUser();
                for (IPermissionService permissionService : permissionServices) {
                    permissions.addAll(permissionService.findPermissionsBySmart(pfAcc,request));
                }
            }else{
                bSysUser = (BSysUser) UserUtils.getCurrentUser();
                //鏅轰汉鏌ヨ鏁版嵁鏉冮檺
                for (IPermissionService permissionService : permissionServices) {
                    permissions.addAll(permissionService.findPermissionsByOffice(bSysUser,request));
                }
            }
        }else if ("frame".equals(Constant.getProperty("app.type", ""))) {
            String url = Constant.getProperty("permit_url", "")+"?pfId="+RedisTool.getPfId(request)+"&frameType="+Constant.getProperty("frame.type", "")+"&userId="+UserUtils.getCurrentUserId();
            String result= HttpClientUtils.httpPostRequest(url);
            try {
                if (StringUtils.isNotBlank(result)) {
                    ResponseData data = JSON.parseObject(result, new TypeReference<ResponseData>() {});
                    if (data.getState()==1) {
                        String permitJson=JSON.parseObject(data.getData().toString()).get("permits").toString();
                        List<String> permits=JSON.parseObject(permitJson, new TypeReference< List<String>>() {});
                        if (CollectionUtils.isNotEmpty(permits)) {
                            permissions.addAll(permits);
                        }
                    }
                }
            } catch (JSONException e) {
                //寮曞叆com.alibaba.fastjson.JSONException寮傚父锛涢亣鍒板紓甯稿垯鎶涘嚭鑷畾涔夊紓甯镐俊鎭�
                throw new JSONException("JSON鏁版嵁瑙ｆ瀽寮傚父锛氬紓甯哥被--AbstractAuthorizingRealm;寮傚父鏂规硶--findPermissions();");
            }
        }
        return permissions;
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
            throws AuthenticationException {
        try {
            if (token instanceof DefaultUsernamepasswordToken) {
                if (StringUtils.isNotBlank(((DefaultUsernamepasswordToken) token).getLoginType())) {
                    if (!((DefaultUsernamepasswordToken) token).getLoginType().equals("plat")) {
                        String vc = (String) SecurityUtils.getSubject().getSession()
                                .getAttribute("login" + token.getPrincipal().toString());
                        char[] pwd = ((DefaultUsernamepasswordToken) token).getPassword();
                        String pwdStr = new String(pwd);
                        if (StringUtils.isBlank(vc) || !vc.equals(pwdStr)) {
                            String msg = "Submitted credentials for token [" + token
                                    + "] did not match the expected credentials.";
                            throw new IncorrectCredentialsException(msg);
                        } else {
                            SecurityUtils.getSubject().getSession()
                                    .removeAttribute("login" + token.getPrincipal().toString());
                        }
                    }
                } else {
                    super.assertCredentialsMatch(token, info);
                }
            } else {
                super.assertCredentialsMatch(token, info);
            }
        } catch (IncorrectCredentialsException e) {
            LOGGER.debug(e.getMessage());
            throw new IncorrectCredentialsException("鎺堟潈璁よ瘉澶辫触");
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        Object userObj = request.getSession().getAttribute("loginUser");
        if (userObj == null) {
            throw new UnknownAccountException("鏈煡璐︽埛"); // 娌℃壘鍒板笎鍙�
        }
        //鑾峰彇鐧诲綍鐢ㄦ埛id
        String userId = SsoUtils.ssoType.equals(SsoUtils.smartType)?((PfAcc)userObj).getId():((BSysUser)userObj).getUserId();
        //鑾峰彇鐧诲綍鐢ㄦ埛瀵嗙爜
        String password = SsoUtils.ssoType.equals(SsoUtils.smartType)?((PfAcc)userObj).getPassword():((BSysUser)userObj).getPassword();

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(new Principal(userObj), // 鐢ㄦ埛鍚�
                password, // 瀵嗙爜
                new RedisSimpleByteSource(userId), getName() // realm
                // name
        );
        return authenticationInfo;
    }
    //杩囨护瓒呯骇绠＄悊鍛樻潈闄�
    @Override
    public  boolean isPermitted(PrincipalCollection principals, String permission){
        return isAdmin() ||super.isPermitted(principals,permission);
    }
	
	/*
	 * 閫氳繃瑙掕壊杩囨护鏉冮檺 瓒呯骇绠＄悊鍛�
	 * @Override
	public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
	    return isAdmin()  || super.hasRole(principals, roleIdentifier);
	}*/

    // 濡傛灉鏄鐞嗗憳鎷ユ湁鎵�鏈夌殑璁块棶鏉冮檺
    private boolean isAdmin() {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        boolean flag=userService.isAdmin(request);
        return flag;
    }

    public List<IPermissionService> getPermissionServices() {
        return permissionServices;
    }

    public void setPermissionServices(List<IPermissionService> permissionServices) {
        this.permissionServices = permissionServices;
    }
}
