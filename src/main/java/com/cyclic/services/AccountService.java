package com.cyclic.services;

import com.cyclic.models.base.ScoreBoard;
import com.cyclic.models.base.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by algys on 18.02.17.
 */


@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Service
public interface AccountService {
    int OK = 0;
    int ERROR_DUPLICATE = 1;
    int ERROR_UNDEFINED = 5;

    int addUser(User newUser);

    User getUserById(Long id);

    int updateUserHighscore(String nickname, long score);

    ScoreBoard getScoreBoard(long page);

    User getUserByLogin(String login);

    User getUserByEmail(String email);

    int setUser(User updatedUser);

    List<User> getUsers(int offset, int limit);
}
