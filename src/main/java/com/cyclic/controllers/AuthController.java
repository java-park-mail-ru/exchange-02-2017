package com.cyclic.controllers;

import com.cyclic.configs.HttpServerConfiguration;
import com.cyclic.models.base.Status;
import com.cyclic.models.base.User;
import com.cyclic.services.AccountService;
import com.cyclic.services.AccountServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by algys on 11.02.17.
 */


@SuppressWarnings({"WeakerAccess", "DefaultFileTemplate"})
@RestController
@RequestMapping(path = "/api/login")
public class AuthController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AccountServiceDB accountService,
                          PasswordEncoder passwordEncoder){
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
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
        if(!passwordEncoder.matches(password, user.getPassword())){
            return ResponseEntity.badRequest().body(new Status("incorrect password"));
        }

        httpSession.setAttribute("userId", user.getId());
        //authorizationService.add(httpSession, user.getId());

        return ResponseEntity.ok(new Status("success login"));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity exit(HttpSession httpSession) {
        if(httpSession.getAttribute("userId")==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        httpSession.removeAttribute("userId");
        //authorizationService.remove(httpSession);

        return ResponseEntity.ok(new Status("success exited"));
    }


}
