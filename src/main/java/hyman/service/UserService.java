package hyman.service;

import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    User getByname(String name);

    List<Roles> getRoles(Integer userid);

    List<Permission> getPermis(List<Integer> roleIds);

    List<Roles> getRolesByName(String username);
}
