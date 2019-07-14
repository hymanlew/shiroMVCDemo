package hyman.utils;

import hyman.entity.User;
import org.apache.shiro.SecurityUtils;

public class UserUtils {

    public static String getCurrentUserId(){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return user.getId().toString();
    }
}
