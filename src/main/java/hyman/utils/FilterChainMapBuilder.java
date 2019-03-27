package hyman.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class FilterChainMapBuilder {

    /**
     * shiro 连接约束配置，即过滤链的定义（但要特别注意）：
     * 权限匹配顺序，采取第一次匹配优先的方式。即如果是 /**=authc 在第一行，则之后的所有访问都必须登录成功，即后面的 url 权限
     * 匹配全部失效。
     *
     * authc，基于表单的拦截器，如果没有登录访问则会跳到对应的登录页面。且必须是认证通过才可访问的，即记住我途径不可访问。
     * logout，退出拦截器，主要属性是 redirectURL，即退出后跳转的页面。
     * user，用户拦截器，用户已经身份验证或是记住我登录的都可，如 "*//**=user"
     * anon，匿名拦截器，即不需要登录就可访问。
     * @return
     */
    public LinkedHashMap<String,String> buildFilterChainMap(){
        LinkedHashMap<String,String> map = new LinkedHashMap();
        // 模拟访问数据库
        map.put("/login","anon");
        map.put("/logout","anon");
        map.put("/logout2","logout");
        map.put("/auth/admin*","authc");
        map.put("/auth/teach","authc,roles[teacher]");
        map.put("/auth/stud","authc,perms['user:update']");

        map.put("/auth/user","user");
        map.put("/**", "authc");
        return map;
    }
}
