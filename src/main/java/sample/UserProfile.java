package sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by algys on 18.02.17.
 */

public class UserProfile {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String login;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonIgnore
    private byte[] password;
    @JsonProperty
    private String email;

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    @JsonCreator
    public UserProfile(String firstName, String lastName, String email, String login, byte[] password){
        this.id = ID_GENERATOR.getAndIncrement();
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password  = password;
        this.email = email;
    }

    public UserProfile(){
        this.id = ID_GENERATOR.getAndIncrement();
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

    public void setPassword(byte[] password){
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

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public byte[] getPassword(){
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
        UserProfile profile = (UserProfile) o;
        return Objects.equals(this.id, profile.id) &&
                Objects.equals(this.firstName, profile.firstName) &&
                Objects.equals(this.lastName, profile.lastName) &&
                Objects.equals(this.email, profile.email) &&
                Arrays.equals(this.password, profile.password) &&
                Objects.equals(this.login, profile.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, login, password);
    }

}
