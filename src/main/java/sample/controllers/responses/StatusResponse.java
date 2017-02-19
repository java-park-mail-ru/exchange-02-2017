package sample.controllers.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by algys on 19.02.17.
 */
public class StatusResponse{
    private String status;

    public StatusResponse(String status){
        this.status = status;
    }

    @JsonProperty("status")
    public String getStatus(){
        return this.status;
    }
}
