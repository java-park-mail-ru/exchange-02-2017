package sample.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by algys on 18.02.17.
 */

@SuppressWarnings("ALL")
public class UserView {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String login;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;


    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    @JsonCreator
    public UserView(@JsonProperty("firstName") String firstName,
                    @JsonProperty("lastName") String lastName,
                    @JsonProperty("email") String email,
                    @JsonProperty("login") String login){
        this.id = ID_GENERATOR.getAndIncrement();
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    UserView(User user){
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public UserView(){}

    public void setLogin(String login){
        this.login = login;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getLogin(){
        return login;
    }

    public Long getId(){
        return id;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

}
