package sample.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sample.models.Status;
import sample.services.AccountService;
import sample.models.User;
import sample.services.AuthorizationService;

import javax.servlet.http.HttpSession;

/**
 * Created by algys on 11.02.17.
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
@RequestMapping(path = "/api/login")
public class AuthController {

    private final AccountService accountService;
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthController(AccountService accountService, AuthorizationService authorizationService){
        this.accountService = accountService;
        this.authorizationService = authorizationService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity tryAuth(@RequestBody User body, HttpSession httpSession) {
        String login = body.getLogin();
        String password = body.getPassword();

        if(login == null){
            return ResponseEntity.badRequest().body(new Status("incorrect login"));
        }
        login = login.trim();

        User user = accountService.getUserByLogin(login);
        if(user == null){
            return ResponseEntity.badRequest().body(new Status("incorrect login"));
        }
        if(!user.getPassword().equals(password)){
            return ResponseEntity.badRequest().body(new Status("incorrect password"));
        }

        httpSession.setAttribute("userId", user.getId().toString());
        authorizationService.add(user.getId());

        return ResponseEntity.ok(new Status("success login"));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity exit(HttpSession httpSession) {
        if(httpSession.getAttribute("userId")==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        Long userId = Long.parseLong(httpSession.getAttribute("userId").toString());
        httpSession.removeAttribute("userId");
        authorizationService.remove(userId);

        return ResponseEntity.ok(new Status("success exited"));
    }


}
