package hyman.entity;

import java.io.Serializable;

// 对于缓存的对象必须实现serizable接口
public class Permission implements Serializable{

    private Integer id;
    private String name;
    private Integer roleId;

    public Permission() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
