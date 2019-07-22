package hyman.dao;

import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;

import java.util.*;

/**
 * 自定义 sql 查询映射方法
 */
public interface UserDao extends BaseMapper<User> {

    User getByname(String name);

    List<Roles> getRoles(Integer userid);

    List<Permission> getPermis(List<Integer> roleIds);

    List<Roles> getRolesByName(String username);

    //int getCount(@Param("vo") BSysLanguage bSysLanguage);
}
