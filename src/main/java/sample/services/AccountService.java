package sample.services;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import sample.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by algys on 18.02.17.
 */


@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public interface AccountService {
    void addUser(User newUser) throws DataAccessException;
    Boolean hasUser(String login);
    User getUserById(Long id);
    User getUserByLogin(String login);
    User getUserByEmail(String email);
    void setUser(User updatedUser) throws DataAccessException;
    List<User> getUsers();
}
