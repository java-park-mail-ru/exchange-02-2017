package sample.controllers.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by algys on 19.02.17.
 */

public class RegRequest{
    private String login;
    private String password;
    private String email;
    private String firstName;
    private String lastName;


    @JsonCreator
    RegRequest(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
               @JsonProperty("email") String email, @JsonProperty("login") String login, @JsonProperty("password") String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getLogin(){
        return this.login;
    }
}