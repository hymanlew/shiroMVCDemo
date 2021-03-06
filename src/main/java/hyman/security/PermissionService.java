package hyman.security;

import hyman.entity.Permission;
import hyman.service.UserService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionService {

    @Resource(name = "service")
    private UserService userService;

    public Set<String> findPermissions(List<Integer> ids) {

        Set<String> permitsSet = new HashSet<String>();
        if (null != ids && !ids.isEmpty()) {
            List<Permission> permissionList = userService.getPermis(ids);
            for(Permission permission : permissionList){
                permitsSet.add(permission.getName());
            }
            return permitsSet;
        }
        return new HashSet<String>();
    }
}
