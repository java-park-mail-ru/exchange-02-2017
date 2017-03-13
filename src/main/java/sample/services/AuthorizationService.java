package sample.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by algys on 12.03.17.
 */

@Service
public class AuthorizationService {
    private HashSet<Long> authorizedUsers = new HashSet<>();

    public void add(Long userId){
        authorizedUsers.add(userId);
    }

    public void remove(Long userId){
        authorizedUsers.remove(userId);
    }

    public boolean isLogged(Long userId){
        return authorizedUsers.contains(userId);
    }
}
