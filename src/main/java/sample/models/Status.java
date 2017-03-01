package sample.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by algys on 19.02.17.
 */
@SuppressWarnings("ALL")
public class Status {
    private String status;

    public Status(String status){
        this.status = status;
    }

    @JsonProperty("status")
    public String getStatus(){
        return this.status;
    }
}
