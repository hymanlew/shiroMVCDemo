package hyman.utils;

import hyman.entity.User;
import org.apache.shiro.SecurityUtils;

public class UserUtils {

    public static String getCurrentUserId(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return user.getId().toString();
    }

    public static String getCurrentSessionAttr(){
        String id = SecurityUtils.getSubject().getSession().getAttribute("identityId").toString();
        return id;
    }
}
