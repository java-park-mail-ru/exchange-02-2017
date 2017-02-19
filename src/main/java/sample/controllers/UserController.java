package sample.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import sample.UserProfile;
import sample.controllers.requests.RegRequest;
import sample.controllers.responses.StatusResponse;
import sample.services.AccountService;
import sample.validators.Validator;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Created by algys on 19.02.17.
 */

@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final AccountService accountService;

    @Autowired
    public UserController(AccountService accountService){
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<StatusResponse> registration(@RequestBody RegRequest body){
        String firstName = body.getFirstName();
        String lastName = body.getLastName();
        String email = body.getEmail();
        String login = body.getLogin().trim();
        byte[] password = DigestUtils.md5Digest(body.getPassword().getBytes());

        if(login == null || !Validator.login(login)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse("invalid login"));
        }

        if(password == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse("invalid password"));
        }

        if(email == null || !Validator.email(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse("invalid email"));
        }

        if(accountService.hasUser(login)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse("login busy"));
        }

        UserProfile newUser = new UserProfile(firstName, lastName, email, login, password);
        accountService.addUser(newUser);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse("success registration"));
    }

    @RequestMapping(method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<ArrayList<UserProfile>> getUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getUsers());
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<UserProfile> getUser(@PathVariable(name = "userId") String userId){
        if(accountService.getUserById(userId) == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(accountService.getUserById(userId));
    }


    @RequestMapping(path = "/{userId}", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<StatusResponse> changeUser(@PathVariable(name = "userId") String userId,
                                                      @RequestBody RegRequest body, HttpSession httpSession){

        if(!CheckPermissions.check(httpSession, userId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("permission denied"));
        }

        if(body.getEmail() != null) {
            if (!Validator.email(body.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("bad email"));
            }

            UserProfile user = accountService.getUserById(userId);
            user.setEmail(body.getEmail());
            accountService.setUser(user);
        }

        if(body.getLogin() != null) {
            if (!Validator.login(body.getLogin())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("bad login"));
            }

            UserProfile user = accountService.getUserById(userId);
            user.setLogin(body.getLogin());
            accountService.setUser(user);
        }

        if(body.getPassword() != null) {
            if (!Validator.password(body.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("bad password"));
            }

            UserProfile user = accountService.getUserById(userId);
            user.setPassword(DigestUtils.md5Digest(body.getPassword().getBytes()));
            accountService.setUser(user);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse("success changing"));
    }

    private static final class CheckPermissions{
        public static Boolean check(HttpSession httpSession, String userId){
            if(httpSession.getAttribute("userId") == null)
                return false;
            return userId.equals((String) httpSession.getAttribute("userId"));
        }
    }
}
