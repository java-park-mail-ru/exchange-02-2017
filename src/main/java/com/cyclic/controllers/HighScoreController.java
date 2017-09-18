package com.cyclic.controllers;

import com.cyclic.Application;
import com.cyclic.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by serych on 19.05.17.
 */
@SuppressWarnings({"WeakerAccess", "DefaultFileTemplate"})
@RestController
@RequestMapping(path = "/api/scoreboard")
public class HighScoreController {

    private final AccountService accountService;

    @Autowired
    public HighScoreController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getScoreBoard(@RequestParam(name = "page") Long page, HttpSession httpSession) {
        if (page == null || page < 1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(Application.gson.toJson(accountService.getScoreBoard(page)));
    }
}