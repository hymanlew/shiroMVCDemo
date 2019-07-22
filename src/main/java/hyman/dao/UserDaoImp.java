package hyman.dao;

import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;
import hyman.entity.UserRoles;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component("userdao")
public class UserDaoImp implements UserDao {

    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate sessionTemplate;

    @Override
    public User getByname(String name) {
        return sessionTemplate.selectOne("userMapper.find",name);
    }

    @Override
    public List<Roles> getRoles(Integer userid) {
        List<Roles> roles = new ArrayList<>();
        List<UserRoles> userRoles = sessionTemplate.selectList("userRolesMapper.find",userid);
        for(UserRoles userRo : userRoles){
            Integer id = userRo.getRole_id();
            Roles role = sessionTemplate.selectOne("rolesMapper.find",id);
            roles.add(role);
        }
        return roles;
    }

    @Override
    public List<Permission> getPermis(List<Integer> roleIds) {
        return sessionTemplate.selectList("permissMapper.find",roleIds);
    }

    @Override
    public List<Roles> getRolesByName(String username) {
        List<Roles> roles = new ArrayList<>();
        List<UserRoles> userRoles = sessionTemplate.selectList("userRolesMapper.findByUserName",username);
        for(UserRoles userRo : userRoles){
            Integer id = userRo.getRole_id();
            Roles role = sessionTemplate.selectOne("rolesMapper.find",id);
            roles.add(role);
        }
        return roles;
    }

    @Override
    public int deleteByPrimaryKey(Object o) {
        return 0;
    }

    @Override
    public int delete(User user) {
        return 0;
    }

    @Override
    public int insert(User user) {
        return 0;
    }

    @Override
    public int insertSelective(User user) {
        return 0;
    }

    @Override
    public List<User> selectAll() {
        return null;
    }

    @Override
    public User selectByPrimaryKey(Object o) {
        return null;
    }

    @Override
    public int selectCount(User user) {
        return 0;
    }

    @Override
    public List<User> select(User user) {
        return null;
    }

    @Override
    public User selectOne(User user) {
        return null;
    }

    @Override
    public int updateByPrimaryKey(User user) {
        return 0;
    }

    @Override
    public int updateByPrimaryKeySelective(User user) {
        return 0;
    }

    @Override
    public int deleteByExample(Object o) {
        return 0;
    }

    @Override
    public List<User> selectByExample(Object o) {
        return null;
    }

    @Override
    public int selectCountByExample(Object o) {
        return 0;
    }

    @Override
    public int updateByExample(User user, Object o) {
        return 0;
    }

    @Override
    public int updateByExampleSelective(User user, Object o) {
        return 0;
    }

    @Override
    public List<User> selectByExampleAndRowBounds(Object o, RowBounds rowBounds) {
        return null;
    }

    @Override
    public List<User> selectByRowBounds(User user, RowBounds rowBounds) {
        return null;
    }
}
