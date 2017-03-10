package sample.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sample.models.User;
import sample.models.Status;
import sample.services.AccountService;
import sample.validators.Validator;

import javax.servlet.http.HttpSession;

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
        origins = "*"
)
@RequestMapping(path = "/api/user")
public class UserController {

    private final AccountService accountService;

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
            return ResponseEntity.badRequest().body(new Status("invalid login"));
        }
        login = login.trim();

        if(password == null){
            return ResponseEntity.badRequest().body(new Status("invalid password"));
        }
        if(email == null || !Validator.email(email)){
            return ResponseEntity.badRequest().body(new Status("invalid email"));
        }

        if(accountService.getUserByLogin(login) != null){
            return ResponseEntity.badRequest().body(new Status("login already used"));
        }
        if(accountService.getUserByEmail(email) != null){
            return ResponseEntity.badRequest().body(new Status("email already used"));
        }

        accountService.addUser(body);
        return ResponseEntity.ok(new Status("success registration"));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(HttpSession httpSession){
        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getUserById((String) httpSession.getAttribute("userId")).toView());
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(@PathVariable(name = "userId") String userId,
                                               HttpSession httpSession){

        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }

        if(accountService.getUserById(userId) == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status("user not exist"));

        return ResponseEntity.ok(accountService.getUserById(userId).toView());
    }


    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity changeUser(@RequestBody User body, HttpSession httpSession){

        if(httpSession.getAttribute("userId") == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        String userId = (String) httpSession.getAttribute("userId");
        User user = accountService.getUserById(userId);

        if(body.getEmail() != null) {
            if (!Validator.email(body.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("incorrect email"));
            }
            if(accountService.getUserByEmail(body.getEmail()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("email already used"));
            }
            user.setEmail(body.getEmail());
        }
        if(body.getLogin() != null) {
            if (!Validator.login(body.getLogin())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("incorrect login"));
            }
            if(accountService.getUserByLogin(body.getLogin()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("login already used"));
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
        return ResponseEntity.status(HttpStatus.OK).body(new Status("success changing"));
    }
}
