package sample.models;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by algys on 19.02.17.
 */


@SuppressWarnings("DefaultFileTemplate")
public class Status {
    public static Integer OK = 0;
    public static Integer ERROR_PASSWORD = 1;
    public static Integer ERROR_LOGIN = 2;
    public static Integer ERROR_EMAIL = 3;
    public static Integer ERROR_UNAUTHORIZED = 4;

    private final Integer code;
    private final String description;

    public Status(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    @SuppressWarnings("unused")
    @JsonProperty("code")
    public Integer getCode(){
        return this.code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
}
