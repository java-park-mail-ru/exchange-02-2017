package com.cyclic.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by algys on 18.02.17.
 */


@SuppressWarnings({"unused", "DefaultFileTemplate"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class UserView {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    @JsonProperty
    private Long id;
    @JsonProperty
    private String login;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;

    @JsonCreator
    public UserView(@JsonProperty("firstName") String firstName,
                    @JsonProperty("lastName") String lastName,
                    @JsonProperty("email") String email,
                    @JsonProperty("login") String login) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    UserView(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public UserView() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
