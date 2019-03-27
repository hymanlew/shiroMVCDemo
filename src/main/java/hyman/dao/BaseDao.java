package hyman.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class BaseDao<T> {

    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate sessionTemplate;

    private Class<T> entityClass = null;
    private String classname = "";

    public BaseDao(){

        // 获取当前类对象
        Class c = getClass();
        // 获取当前类对象的超类对象
        Type type = c.getGenericSuperclass();
        // 如果可以向上造型为参数化类型，即是同一实现的实体
        if(type instanceof ParameterizedType){

            // 获取当前超类对象的参数对象，即实体类对象数组
            Type[] parameter = ((ParameterizedType)type).getActualTypeArguments();
            // 拿出实例对象
            this.entityClass = (Class<T>) parameter[0];

            // 类名是完整路径的名字，需要处理
            String name = entityClass.getName();
            this.classname = name.substring(name.lastIndexOf(".")+1);
        }
    }

    public T get(Integer id){
        return sessionTemplate.selectOne(this.classname.toLowerCase()+"getById",id);
    }

    public String save(T entity){
        return sessionTemplate.insert(this.classname.toLowerCase()+"insert",entity)+"";
    }

    public List<T> getList(Integer id){
        return sessionTemplate.selectList(this.classname.toLowerCase()+"getListById",id);
    }
}
