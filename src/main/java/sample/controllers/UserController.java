package sample.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sample.models.User;
import sample.models.Status;
import sample.services.AccountService;
import sample.validators.Validator;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by algys on 19.02.17.
 */


@SuppressWarnings({"WeakerAccess", "DefaultFileTemplate"})
@RestController
@CrossOrigin(
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        maxAge = 3600,
        allowedHeaders = {"Content-Type", "Origin", "X-Requested-With", "Accept"},
        allowCredentials = "true",
        origins = "http://editor.swagger.io"
)
@RequestMapping(path = "/api/user")
public class UserController {

    private final AccountService accountService;
    private final ObjectMapper jmap = new ObjectMapper();

    @Autowired
    public UserController(AccountService accountService){
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity registration(@RequestBody User body){
        String email = body.getEmail();
        String login = body.getLogin();
        String password = body.getPassword();

        if(login == null || !Validator.login(login.trim())){
            return ResponseEntity.ok(new Status(Status.ERROR_LOGIN, "invalid login"));
        }
        login = login.trim();

        if(password == null){
            return ResponseEntity.ok(new Status(Status.ERROR_PASSWORD, "invalid password"));
        }
        if(email == null || !Validator.email(email)){
            return ResponseEntity.ok(new Status(Status.ERROR_EMAIL, "invalid email"));
        }

        if(accountService.getUserByLogin(login) != null){
            return ResponseEntity.ok(new Status(Status.ERROR_LOGIN, "login already used"));
        }
        if(accountService.getUserByEmail(email) != null){
            return ResponseEntity.ok(new Status(Status.ERROR_EMAIL, "email already used"));
        }

        accountService.addUser(body);
        return ResponseEntity.ok(new Status(Status.OK,"success registration"));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(HttpSession httpSession){
        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.ok(new Status(Status.ERROR_UNAUTHORIZED, "user not authorized"));
        }
        try {
            return ResponseEntity.ok(new Status(Status.OK, accountService.getUserById((String) httpSession.getAttribute("userId")).toView().getAsJSON()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(@PathVariable(name = "userId") String userId,
                                               HttpSession httpSession) throws IOException {

        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.ok(new Status(Status.ERROR_UNAUTHORIZED, "user not authorized"));
        }

        if(accountService.getUserById(userId) == null)
            return ResponseEntity.ok(new Status(Status.OK, "user not exist"));

        try {
            return ResponseEntity.ok(new Status(Status.OK, accountService.getUserById(userId).toView().getAsJSON()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }


    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity changeUser(@RequestBody User body, HttpSession httpSession){

        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.ok(new Status(Status.ERROR_UNAUTHORIZED, "user not authorized"));
        }
        String userId = (String) httpSession.getAttribute("userId");
        User user = accountService.getUserById(userId);

        if(body.getEmail() != null) {
            if (!Validator.email(body.getEmail())) {
                return ResponseEntity.ok(new Status(Status.ERROR_EMAIL, "incorrect email"));
            }
            if(accountService.getUserByEmail(body.getEmail()) != null){
                return ResponseEntity.ok(new Status(Status.ERROR_EMAIL, "email already used"));
            }
            user.setEmail(body.getEmail());
        }
        if(body.getLogin() != null) {
            if (!Validator.login(body.getLogin())) {
                return ResponseEntity.ok(new Status(Status.ERROR_LOGIN, "incorrect login"));
            }
            if(accountService.getUserByLogin(body.getLogin()) != null){
                return ResponseEntity.ok(new Status(Status.ERROR_LOGIN, "login already used"));
            }
            user.setLogin(body.getLogin());
        }
        if(body.getFirstName() != null) {
            user.setFirstName(body.getFirstName());
        }
        if(body.getLastName() != null) {
            user.setLastName(body.getLastName());
        }
        if(body.getPassword() != null) {
            user.setPassword(body.getPassword());
        }

        accountService.setUser(user);
        return ResponseEntity.ok(new Status(Status.OK, "success changing"));
    }
}
