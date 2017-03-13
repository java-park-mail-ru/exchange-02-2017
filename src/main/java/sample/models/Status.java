package sample.models;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by algys on 19.02.17.
 */


@SuppressWarnings("DefaultFileTemplate")
public class Status {
    private final String status;

    public Status(String status){
        this.status = status;
    }

    @SuppressWarnings("unused")
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
}
