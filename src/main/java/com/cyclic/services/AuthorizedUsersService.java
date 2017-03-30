package com.cyclic.services;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by algys on 12.03.17.
 */

@SuppressWarnings("DefaultFileTemplate")
@Service
public class AuthorizedUsersService {
    private final ConcurrentHashMap<HttpSession, Long> authorizedUsers = new ConcurrentHashMap<>();

    public void add(HttpSession httpSession, Long userId){
        authorizedUsers.put(httpSession, userId);
    }

    public void remove(HttpSession httpSession){
        authorizedUsers.remove(httpSession);
    }

    @SuppressWarnings("unused")
    public boolean isLogged(HttpSession httpSession){
        return authorizedUsers.containsKey(httpSession);
    }
}
