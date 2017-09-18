package com.cyclic.models.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by algys on 19.02.17.
 */


@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class Status {

    @JsonProperty
    private final String status;

    @JsonCreator
    public Status(@JsonProperty("status") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
