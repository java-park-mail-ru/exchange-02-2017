package sample.services;

import org.springframework.stereotype.Service;
import sample.models.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by algys on 18.02.17.
 */


@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public class AccountService {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    public void addUser(User newUser){
        newUser.setId(ID_GENERATOR.getAndIncrement());
        users.put(newUser.getId(), newUser);
    }

    public Boolean hasUser(String login){
        for (ConcurrentHashMap.Entry<Long, User> pair : users.entrySet()) {
            if(pair.getValue().getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public User getUserById(String id){
        return users.get(Long.parseLong(id));
    }

    public User getUserByLogin(String login){
        for (ConcurrentHashMap.Entry<Long, User> pair : users.entrySet()) {
            if(pair.getValue().getLogin().equals(login)){
                return pair.getValue();
            }
        }
        return null;
    }

    public User getUserByEmail(String email){
        for (ConcurrentHashMap.Entry<Long, User> pair : users.entrySet()) {
            if(pair.getValue().getEmail().equals(email)){
                return pair.getValue();
            }
        }
        return null;
    }

    public void setUser(User updatedUser){
        users.remove(updatedUser.getId());

        users.put(updatedUser.getId(), updatedUser);
    }

    public ArrayList<User> getUsers(){
        return new ArrayList<>(users.values());
    }
}
