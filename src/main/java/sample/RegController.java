package sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by algys on 19.02.17.
 */

@RestController
@RequestMapping(path = "/registration")
public class RegController {
    private final AccountService accountService;

    @Autowired
    public RegController(AccountService accountService){
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public RegResponse tryReg(@RequestBody RegRequest body, HttpSession httpSession) {
        String firstName = body.getFirstName();
        String lastName = body.getLastName();
        String email = body.getEmail();
        String login = body.getLogin();
        byte[] password = DigestUtils.md5Digest(body.getPassword().getBytes());

        if(accountService.hasUser(login)){
            return new RegResponse("false", "login busy");
        }

        UserProfile newUser = new UserProfile(firstName, lastName, email, login, password);
        accountService.addUser(newUser);

        httpSession.setAttribute("userId", newUser.getId().toString());
        return new RegResponse("true", "success");
    }

    private static final class RegRequest{
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

        String getFirstName(){
            return this.firstName;
        }

        String getLastName(){
            return this.lastName;
        }

        String getEmail(){
            return this.email;
        }

        String getPassword(){
            return this.password;
        }

        String getLogin(){
            return this.login;
        }
    }

    private static final class RegResponse{
        private String status;
        private String description;

        RegResponse(String status, String description){
            this.status = status;
            this.description = description;
        }

        @JsonProperty("status")
        public String getStatus(){
            return this.status;
        }

        @JsonProperty("description")
        public String getDescription(){
            return this.description;
        }
    }
}
