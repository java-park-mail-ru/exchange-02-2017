package sample.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sample.services.AccountService;
import sample.UserProfile;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

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
    public ResponseEntity<AuthResponse> tryAuth(@RequestBody AuthRequest body, HttpSession httpSession) {
        String login = body.getLogin().trim();
        byte[] password = DigestUtils.md5Digest(body.getPassword().getBytes());

        if(login == null || login.length()<6 | login.length()>12){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("null"));
        }

        UserProfile user = accountService.getUserByLogin(login);
        if(user == null || !Arrays.equals(user.getPassword(), password)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("null"));
        }

        httpSession.setAttribute("userId", user.getId().toString());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(user.getId().toString()));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<AuthResponse> checkAuth(HttpSession httpSession) {
        Object userId = httpSession.getAttribute("userId");

        if(userId != null){
            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse((String)userId));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("null"));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<AuthResponse> exit(HttpSession httpSession) {
        httpSession.setAttribute("userId", null);
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse("null"));
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
        private String userId;

        AuthResponse(String userId){
            this.userId = userId;
        }

        @JsonProperty("userId")
        public String getUserId(){
            return this.userId;
        }
    }

}
