package hyman.entity;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

// 对于缓存的对象必须实现serizable接口
public class User implements Serializable{

    /**
     * Bean Validation 中内置的 constraint： 
     @Null   被注释的元素必须为 null
     @NotNull    被注释的元素必须不为 null     
     @AssertTrue     被注释的元素必须为 true     
     @AssertFalse    被注释的元素必须为 false     
     @Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值     
     @Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值     
     @DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值     
     @DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值     
     @Size(max=, min=)   被注释的元素的大小必须在指定的范围内     
     @Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内     
     @Past   被注释的元素必须是一个过去的日期     
     @Future     被注释的元素必须是一个将来的日期     
     @Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式     

     Hibernate Validator 附加的 constraint：
     @NotBlank(message =)   验证字符串非null，且长度必须大于0     
     @Email  被注释的元素必须是电子邮箱地址     
     @Length(min=,max=)  被注释的字符串的大小必须在指定的范围内     
     @NotEmpty   被注释的字符串的必须非空     
     @Range(min=,max=,message=)  被注释的元素必须在合适的范围内  
     @CreditCardNumber     字符串必须通过Luhn校验算法（例如银行卡）
     */

    private Integer id;

    @NotBlank(message="${error.name.blank}")
    private String name;

    @NotEmpty(message="${error.pass.blank}")
    @Length(min=6,message="${error.passlenth}")
    private String password;

    @Max(value=100,message="${error.age.max}")
    @Min(value=18,message="${error.age.min}")
    private Integer age;

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
