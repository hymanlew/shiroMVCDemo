package hyman.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Logutil {

    public static final Logger logger = LoggerFactory.getLogger(Logutil.class);

    public static void contextLoads() {
        /**
         * 日志的级别由低到高，trace < debug < info < warn < error。可以调整输出的日志级别；日志就只会在这个级别以以后的高级
         * 别生效。
         */
        logger.trace("这是trace日志，跟踪信息...");
        logger.debug("这是debug日志，程序调试...");

        //SpringBoot默认给我们使用的是info级别的，没有指定级别的就用 SpringBoot默认规定的级别；即 root 指定的级别。
        logger.info("这是info日志，自定义的信息...");
        logger.warn("这是warn日志，记录警告...");
        logger.error("这是error日志，记录错误异常...");
    }

    public static void getValidData(Map<String, Object> map, BindingResult result) {
        List<ObjectError> errorList = result.getAllErrors();
        Set<String> errors = new LinkedHashSet<>();
        for(ObjectError error : errorList){
            Logutil.logger.error("=== 工人错误信息："+error.getDefaultMessage());
            errors.add(error.getDefaultMessage());

        }
        map.put("error",errors);
    }

    public static void main(String[] args) {
        contextLoads();
    }
}
