package com.cyclic.services;

import com.cyclic.models.base.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Created by algys on 26.03.17.
 */

@SuppressWarnings("DefaultFileTemplate")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class AccountServiceDBTest {
    @Autowired
    private AccountServiceDB accountService;

    @Test
    public void addUserTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");
        assertEquals(AccountService.OK, accountService.addUser(newUser));
    }

    @Test
    public void addUserDuplicateTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");

        assertEquals(AccountService.OK, accountService.addUser(newUser));
        assertEquals(AccountService.ERROR_DUPLICATE, accountService.addUser(newUser));
    }

    @Test
    public void getUserByLoginTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");
        accountService.addUser(newUser);
        User user = accountService.getUserByLogin(newUser.getLogin());
        assertNotNull(user);
    }

    @Test
    public void getUserByIdTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");
        accountService.addUser(newUser);
        long id = accountService.getUserByLogin(newUser.getLogin()).getId();
        User user = accountService.getUserById(id);
        assertNotNull(user);
    }

    @Test
    public void getUserByEmailTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");
        accountService.addUser(newUser);
        User user = accountService.getUserByEmail(newUser.getEmail());
        assertNotNull(user);
    }

    @Test
    public void setUserTest() {
        User newUser = new User(null, null, null, "test@test.com", "test", "test");
        accountService.addUser(newUser);

        User user = accountService.getUserByLogin(newUser.getLogin());
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("new@test.com");
        user.setLogin("new");
        user.setPassword("new");

        assertEquals(AccountService.OK, accountService.setUser(user));

        User updUser = accountService.getUserById(user.getId());
        assertEquals(user.getId(), updUser.getId());
        assertEquals(user.getLogin(), updUser.getLogin());
        assertEquals(user.getEmail(), updUser.getEmail());
        assertEquals(user.getFirstName(), updUser.getFirstName());
        assertEquals(user.getLastName(), updUser.getLastName());
    }

    @Test
    public void setUserDuplicateTest() {
        User newUser1 = new User(null, null, null, "test@test.com", "test", "test");
        User newUser2 = new User(null, null, null, "test2@test.com", "test2", "test2");
        accountService.addUser(newUser1);
        accountService.addUser(newUser2);

        User user;

        user = accountService.getUserByLogin(newUser1.getLogin());
        user.setLogin(newUser2.getLogin());
        assertEquals(AccountService.ERROR_DUPLICATE, accountService.setUser(user));

        user = accountService.getUserByLogin(newUser1.getLogin());
        user.setEmail(newUser2.getEmail());
        assertEquals(AccountService.ERROR_DUPLICATE, accountService.setUser(user));
    }

    @Test
    public void getUsersTest() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String email = String.valueOf(i) + "@" + String.valueOf(i) + ".com";
            String login = String.valueOf(i);
            String password = String.valueOf(i);
            User newUser = new User(null, null, null, email, login, password);
            accountService.addUser(newUser);
            users.add(accountService.getUserByLogin(login));
        }

        int limit, offset;
        Random rand = new Random();
        for (int k = 0; k < 10; k++) {
            offset = rand.nextInt(49);
            limit = rand.nextInt(50 - offset);
            List<User> test = accountService.getUsers(offset, limit);
            for (int i = 0; i < limit; i++) {
                assertEquals(users.get(offset + i).getId(), test.get(i).getId());
                assertEquals(users.get(offset + i).getLogin(), test.get(i).getLogin());
                assertEquals(users.get(offset + i).getEmail(), test.get(i).getEmail());
                assertEquals(users.get(offset + i).getPassword(), test.get(i).getPassword());
            }
        }
    }
}
