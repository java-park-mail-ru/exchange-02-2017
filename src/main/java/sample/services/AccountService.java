package sample.services;

import org.springframework.stereotype.Service;
import sample.UserProfile;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by algys on 18.02.17.
 */

@Service
public class AccountService {
    private ConcurrentHashMap<Long, UserProfile> users = new ConcurrentHashMap<>();

    public void addUser(UserProfile newUser){
        users.put(newUser.getId(), newUser);
    }

    public Boolean hasUser(String login){
        for (ConcurrentHashMap.Entry<Long, UserProfile> pair : users.entrySet()) {
            if(pair.getValue().getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public UserProfile getUserById(String id){
        return users.get(Long.parseLong(id));
    }

    public UserProfile getUserByLogin(String login){
        for (ConcurrentHashMap.Entry<Long, UserProfile> pair : users.entrySet()) {
            if(pair.getValue().getLogin().equals(login)){
                return pair.getValue();
            }
        }
        return null;
    }

    public void setUser(UserProfile updatedUser){
        users.remove(updatedUser.getId());
        users.put(updatedUser.getId(), updatedUser);
    }

    public ArrayList<UserProfile> getUsers(){
        return new ArrayList<>(users.values());
    }
}
