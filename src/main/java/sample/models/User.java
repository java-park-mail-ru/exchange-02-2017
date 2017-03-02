package sample.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by algys on 18.02.17.
 */


@SuppressWarnings("DefaultFileTemplate")
public class User {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String login;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonIgnore
    private String password;
    @JsonProperty
    private String email;

    @SuppressWarnings("unused")
    @JsonCreator
    public User(@JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName,
                @JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("password") String password){
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password  = password;
        this.email = email;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getLogin(){
        return login;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User profile = (User) o;
        return Objects.equals(this.id, profile.id) &&
                Objects.equals(this.firstName, profile.firstName) &&
                Objects.equals(this.lastName, profile.lastName) &&
                Objects.equals(this.email, profile.email) &&
                Objects.equals(this.password, profile.password) &&
                Objects.equals(this.login, profile.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, login, password);
    }

    public UserView toView(){
        return new UserView(this);
    }
}
