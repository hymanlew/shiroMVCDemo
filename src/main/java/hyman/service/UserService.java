package hyman.service;

import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;

import java.util.List;
import java.util.Set;

/**
 * 另外自定义业务处理的方法s
 */
public interface UserService extends BaseService<User>{

    User getByname(String name);

    List<Roles> getRoles(Integer userid);

    List<Permission> getPermis(List<Integer> roleIds);

    List<Roles> getRolesByName(String username);
}
