package sample.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sample.models.Status;
import sample.services.AccountService;
import sample.models.User;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Created by algys on 11.02.17.
 */

@SuppressWarnings("ALL")
@RestController
@CrossOrigin(
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        maxAge = 3600,
        allowedHeaders = {"Content-Type"}
)
@RequestMapping(path = "/api/login")
public class AuthController {

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService){
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity tryAuth(@RequestBody User body, HttpSession httpSession) {
        String login = body.getLogin();
        String password = body.getPassword();

        if(login == null || login.trim().length()<6 | login.trim().length()>12){
            return ResponseEntity.badRequest().body(new Status("login incorrect"));
        }
        login = login.trim();

        User user = accountService.getUserByLogin(login);
        if(user == null){
            return ResponseEntity.badRequest().body(new Status("incorrect login"));
        }
        if(!Objects.equals(user.getPassword(), password)){
            return ResponseEntity.badRequest().body(new Status("incorrect password"));
        }

        httpSession.setAttribute("userId", user.getId().toString());
        return ResponseEntity.ok(new Status("success login"));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity exit(HttpSession httpSession) {
        if(httpSession.getAttribute("userId")==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        httpSession.removeAttribute("userId");
        return ResponseEntity.ok(new Status("success exited"));
    }


}
