package com.cyclic.controllers;

import com.cyclic.models.base.Status;
import com.cyclic.models.base.User;
import com.cyclic.models.base.UserView;
import com.cyclic.services.AccountService;
import com.cyclic.services.game.GuestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private GuestManager guestManager;

    @Autowired
    public AuthController(AccountService accountService,
                          PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        guestManager = new GuestManager();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity tryAuth(@RequestBody User body, HttpSession httpSession) {
        if (httpSession.getAttribute("nickname") != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("You are already logged in"));
        }
        String login = body.getLogin();
        String password = body.getPassword();

        if (login == null) {
            return ResponseEntity.badRequest().body(new Status("You need to specify username"));
        }
        login = login.trim();

        User user = accountService.getUserByLogin(login);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new Status("Incorrect password. Try again"));
        }
        httpSession.setAttribute("nickname", user.getLogin());
        //authorizationService.add(httpSession, user.getId());

        return ResponseEntity.ok(user.toView());
    }

    @RequestMapping(path = "/temporary", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity tempUser(HttpSession httpSession) {
        if (httpSession.getAttribute("nickname") != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status("You are already logged in"));
        }
        String name = guestManager.getNewGuestNick();
        httpSession.setAttribute("nickname", name);
        httpSession.setAttribute("guest", true);
        //authorizationService.add(httpSession, user.getId());

        return ResponseEntity.ok(new UserView("", "", "", name, (long) 0));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity exit(HttpSession httpSession) {
        if (httpSession.getAttribute("nickname") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Status("user not authorized"));
        }

        if (httpSession.getAttribute("guest") != null) {
            String nick = (String) httpSession.getAttribute("nickname");
            guestManager.freeGuestNick(nick);
            httpSession.removeAttribute("guest");
        }

        httpSession.removeAttribute("nickname");
        //authorizationService.remove(httpSession);

        return ResponseEntity.ok(new Status("success exited"));
    }


}
