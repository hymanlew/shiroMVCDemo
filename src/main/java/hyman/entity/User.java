package hyman.entity;

import java.io.Serializable;

// 对于缓存的对象必须实现serizable接口
public class User implements Serializable{

    private Integer id;
    private String name;
    private String password;

    public User() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
