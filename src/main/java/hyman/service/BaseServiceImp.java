package hyman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;
import java.util.List;

public abstract class BaseServiceImp<T> implements BaseService<T> {

    /**
     * 国际化资源存取器
     */
    @Resource
    private MessageSourceAccessor messageSourceAccessor;

    /**
     * 数据层接口，import tk.mybatis.mapper.common.Mapper;
     */
    @Autowired
    private Mapper<T> mapper;

    public Mapper<T> getMapper() {
        return mapper;
    }

    /**
     * 根据主键获取对象
     * @param key 含有主键的实体对象
     * @return 查询的对象
     */
    @Override
    public T selectByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    /**
     * 保存方法
     * @param entity 需要保存的实体对象
     * @return 0失败 or 1成功
     */
    @Override
    public int save(T entity) {
        return mapper.insert(entity);
    }

    /**
     * 保存对象中非空数据
     * @param entity 需要保存的实体对象
     * @return 0失败 or 1成功
     */
    @Override
    public int saveNotNull(T entity){
        return mapper.insertSelective(entity);
    }

    /**
     * 删除方法
     * @param key 含有主键的实体对象
     * @return 0失败 or 1成功
     */
    @Override
    public int delete(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    /**
     * 全部更新方法
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    @Override
    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    /**
     * 更新对象中非空数据
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    @Override
    public int updateNotNull(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 查询数据集合方法
     * @param example 实体对象
     * @return 数据集合
     */
    @Override
    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

    /**
     *
     * <p><b>方法描述：</b>获取国际化资源存取器对象</p>
     * @return 国际化资源存取器对象
     */
    public MessageSourceAccessor getMessageSourceAccessor() {
        return messageSourceAccessor;
    }
}
