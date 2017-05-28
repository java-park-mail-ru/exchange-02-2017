package com.cyclic.controllers;

import com.cyclic.models.base.Status;
import com.cyclic.models.base.User;
import com.cyclic.services.AccountService;
import com.cyclic.services.AccountServiceDB;
import com.cyclic.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by algys on 19.02.17.
 */


@SuppressWarnings({"WeakerAccess", "DefaultFileTemplate"})
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(AccountServiceDB accountService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity registration(@RequestBody User body) {
        String email = body.getEmail();
        String login = body.getLogin();
        String password = body.getPassword();
        StringBuilder errorBuilder = new StringBuilder();

        if (login == null || !Validator.login(login.trim())) {
            errorBuilder.append("Login must not be null. ");
        }
        if (email == null || !Validator.email(email)) {
            errorBuilder.append("Email must not be null. ");
        }
        if (password == null || password.length() == 0) {
            errorBuilder.append("Password must not be null. ");
        }
        if (accountService.getUserByLogin(login) != null) {
            errorBuilder.append("This username is already registered. ");
        }
        if (accountService.getUserByEmail(email) != null) {
            errorBuilder.append("This email is already registered. ");
        }

        String error = errorBuilder.toString();
        if (error.length() != 0) {
            return ResponseEntity.badRequest().body(new Status(error));
        }

        login = login.trim();

        body.setPassword(passwordEncoder.encode(password));
        accountService.addUser(body);
        return ResponseEntity.ok(new Status("success registration"));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(HttpSession httpSession) {
        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }

        return ResponseEntity.ok(accountService.getUserById((Long) httpSession.getAttribute("userId")).toView());
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getUser(@PathVariable(name = "userId") Long userId,
                                  HttpSession httpSession) throws IOException {

        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        if (accountService.getUserById(userId) == null)
            return ResponseEntity.badRequest().body(new Status("user not exist"));

        return ResponseEntity.ok(accountService.getUserById(userId).toView());
    }


    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity changeUser(@RequestBody User body, HttpSession httpSession) {

        if (httpSession.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }
        Long userId = (Long) httpSession.getAttribute("userId");
        User user = accountService.getUserById(userId);

        if (body.getEmail() != null) {
            if (!Validator.email(body.getEmail())) {
                return ResponseEntity.badRequest().body(new Status("incorrect email"));
            }
            if (accountService.getUserByEmail(body.getEmail()) != null) {
                return ResponseEntity.badRequest().body(new Status("email already used"));
            }
            user.setEmail(body.getEmail());
        }
        if (body.getLogin() != null) {
            if (!Validator.login(body.getLogin())) {
                return ResponseEntity.badRequest().body(new Status("incorrect login"));
            }
            if (accountService.getUserByLogin(body.getLogin()) != null) {
                return ResponseEntity.badRequest().body(new Status("login already used"));
            }
            user.setLogin(body.getLogin());
        }
        if (body.getFirstName() != null) {
            user.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            user.setLastName(body.getLastName());
        }
        if (body.getPassword() != null && body.getPassword().length() > 0) {
            user.setPassword(passwordEncoder.encode(body.getPassword()));
        }

        accountService.setUser(user);
        return ResponseEntity.ok(new Status("success changing"));
    }
}
