package hyman.service;

import com.github.pagehelper.PageHelper;
import hyman.dao.UserDao;
import hyman.entity.Permission;
import hyman.entity.Roles;
import hyman.entity.User;
import hyman.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component("service")
public class UserServiceImp extends BaseServiceImp<User> implements UserService {

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

    //分页查询
    public List<User> fenye(int current,int rowCount,String sort,String nane,String ph ){
        PageHelper.startPage(current,rowCount);//分页

        // 表示实例化同时传递了一个对象给构造方法, 这个对象是一个Class对象，创建示例对象
        Example example = new Example(User.class);
        example.setOrderByClause(sort);   //排序那个字段

        // 创建示例的查询标准，条件
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(nane)) {
            criteria.andLike("xm", "%" + nane + "%");
        }
        if (StringUtils.isNotEmpty(ph)) {
            criteria.andLike("rybh", "%" + ph + "%");
        }

        // criteria.andEqualTo("xm", "崔颖");//条件相等
        // criteria.andGreaterThan("xb", "1");//大于
        // criteria.andLessThan("xb", "2");//小于
        // criteria.andIsNotNull("xm");//is not null
        // criteria.andCondition("xzdqh=","110104");//加各种条件都可以 = like <,可以代替全部的

        // List<String> values=new ArrayList<String>();
        // values.add("110104");
        // values.add("440304");
        // criteria.andIn("xzdqh", values);//in()

        // criteria.andBetween("csrq", "1956/01/08", "1966/10/21");//时间相隔

        // Example.Criteria criteria2 = example.createCriteria();
        // criteria2.andCondition("xzdqh=","220104");
        // example.or().andCondition("xzdqh=","220104");//or
        // example.or(criteria2);//or

        List<User> list=userDao.selectByExample(example);
        userDao.updateByPrimaryKeySelective(new User());
        return list;
    }

}
