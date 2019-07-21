package hyman.service;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

// 告诉 MethodValidationPostProcessor 此Bean需要开启方法级别验证支持
@Validated
@Service
public class ValidatorService {

    public @Length(min = 12, max = 16, message = "返回值长度应该为12-16") String getContent(
            @NotBlank(message = "name不能为空") String name,
            @Size(min = 6, max = 10, message="${error.passlenth}") String password) {

        return name + ":" + password;
    }
}
