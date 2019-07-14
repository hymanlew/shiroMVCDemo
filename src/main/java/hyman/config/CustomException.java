package hyman.config;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class CustomException extends RuntimeException{

    public CustomException(String msg){
        super(msg);
    }

}
