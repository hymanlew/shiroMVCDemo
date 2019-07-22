package hyman.service;

import java.util.List;

public interface BaseService<T> {
    /**
     * 根据主键查询对象
     * @param key 主键
     * @return 泛型对象
     */
    T selectByKey(Object key);

    /**
     * 保存实体类
     * 	该方法中实体类的每个参数都必须不为空
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    int save(T entity);

    /**
     * 保存实体类
     * 	实体类参数可为空，如果为空则自动忽略
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    int saveNotNull(T entity);

    /**
     *
     * 根据主键删除对象
     * @param key 主键
     * @return 0失败 or 1成功
     */
    int delete(Object key);

    /**
     * 更新对象的全部属性
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    int updateAll(T entity);

    /**
     * 更新对象，如果传入的属性为空，则跳过
     * @param entity 实体对象
     * @return 0失败 or 1成功
     */
    int updateNotNull(T entity);

    /**
     * 根据条件查询列表
     * @param example 实体对象
     * @return 数据列表
     */
    List<T> selectByExample(Object example);
}
