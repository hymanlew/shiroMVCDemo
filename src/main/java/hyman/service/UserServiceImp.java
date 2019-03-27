package hyman.service;

import hyman.dao.UserDao;
import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component("service")
public class UserServiceImp implements UserService {

    @Resource(name = "userdao")
    private UserDao userDao;

    @Override
    public User getByname(String name) {
        return userDao.getByname(name);
    }

    @Override
    @Transactional
    public List<Roles> getRoles(Integer userid) {
        return userDao.getRoles(userid);
    }

    @Override
    @Transactional
    public List<Permission> getPermis(List<Integer> roleIds) {
        return userDao.getPermis(roleIds);
    }

    @Override
    public List<Roles> getRolesByName(String username) {
        return userDao.getRolesByName(username);
    }
}
