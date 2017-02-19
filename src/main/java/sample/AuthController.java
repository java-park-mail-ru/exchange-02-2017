package sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by algys on 11.02.17.
 */

@RestController
@RequestMapping(path = "/login")
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService){
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public AuthResponse tryAuth(@RequestBody AuthRequest body,  HttpSession httpSession) {
        String login = body.getLogin();
        String password = body.getPassword();

        if(login == null || login.trim().length()==0){
            return new AuthResponse("false", "null");
        }

        UserProfile user = accountService.getUserByLogin(login);
        if(user == null || !user.getPassword().equals(password)){
            return new AuthResponse("false", null);
        }

        httpSession.setAttribute("userId", user.getId().toString());
        return new AuthResponse("true", user.getId().toString());
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public AuthResponse checkAuth(HttpSession httpSession) {
        Object userId = httpSession.getAttribute("userId");

        if(userId != null){
            return new AuthResponse("true", (String)userId);
        }
        return new AuthResponse("false", "null");
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public AuthResponse exit(HttpSession httpSession) {
        httpSession.setAttribute("userId", null);

        return new AuthResponse("true", "null");
    }

    private static final class AuthRequest{
        private String login;
        private String password;

        @JsonCreator
        AuthRequest(@JsonProperty("login") String login, @JsonProperty("password") String password){
            this.login = login;
            this.password = password;
        }

        String getPassword(){
            return this.password;
        }

        String getLogin(){
            return this.login;
        }
    }

    private static final class AuthResponse{
        private String status;
        private String userId;

        AuthResponse(String status, String userId){
            this.status = status;
            this.userId = userId;
        }

        @JsonProperty("status")
        public String getStatus(){
            return this.status;
        }

        @JsonProperty("userId")
        public String getUserId(){
            return this.userId;
        }
    }

}
