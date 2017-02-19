package sample;

import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.Iterator;
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

}
