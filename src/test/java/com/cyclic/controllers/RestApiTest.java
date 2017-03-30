package com.cyclic.controllers;

import com.cyclic.models.Status;
import com.cyclic.models.User;
import com.cyclic.models.UserView;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Created by algys on 28.03.17.
 */

@SuppressWarnings({"SameParameterValue", "DefaultFileTemplate", "StringBufferReplaceableByString"})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class RestApiTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final Random rand = new Random();

    private String email;
    private String login;
    private String password;

    @NotNull
    private String getRandomString(int len ){
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( chars.charAt( rand.nextInt(chars.length()) ) );
        return sb.toString();
    }

    @NotNull
    private String getRandomEmail(){
        return new StringBuilder()
                .append(getRandomString(3))
                .append("@")
                .append(getRandomString(3))
                .append(".com")
                .toString();
    }

    @Before
    public void setFields(){
        email = getRandomEmail();
        login = getRandomString(8);
        password = getRandomString(8);
    }

    private ResponseEntity<Status> login(User user, HttpStatus expectedHttpStatus) {
        final ResponseEntity<Status> result = restTemplate.postForEntity("/api/login", user, Status.class);
        assertEquals(expectedHttpStatus, result.getStatusCode());
        return result;
    }

    private void registration(User user, HttpStatus expectedHttpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<Status> result = restTemplate.exchange("/api/user", HttpMethod.PUT, requestEntity, Status.class);
        assertEquals(expectedHttpStatus, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    private void logout(String cookie){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<Status> result = restTemplate.exchange("/api/login", HttpMethod.DELETE, request, Status.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    private ResponseEntity<User> getMe(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity request = new HttpEntity(headers);
        return restTemplate.exchange("/api/user", HttpMethod.GET, request, User.class);
    }

    private void updateUser(String cookie, User user, HttpStatus expectedHttpStatus){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<Status> statusRequestEntity =
                restTemplate.exchange("/api/user", HttpMethod.POST, request, Status.class);
        assertEquals(expectedHttpStatus, statusRequestEntity.getStatusCode());
    }

    @Test
    public void authTest(){
        User user = new User(email, login, password);

        //without registration
        login(user, HttpStatus.BAD_REQUEST);

        //with registration
        registration(user, HttpStatus.OK);
        ResponseEntity<Status> result = login(user, HttpStatus.OK);
        String cookie = result.getHeaders().get("Set-Cookie").get(0);
        logout(cookie);
    }

    @Test
    public void registrationTest(){
        User user;

        //incorrect fields
        user = new User("", login, password);
        registration(user, HttpStatus.BAD_REQUEST);
        user = new User(email, "", password);
        registration(user, HttpStatus.BAD_REQUEST);
        user = new User(email, login, "");
        registration(user, HttpStatus.BAD_REQUEST);

        //success registration
        user = new User(email, login, password);
        registration(user, HttpStatus.OK);
        login(user, HttpStatus.OK);

        //duplicate registration
        registration(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getCurrentUserTest(){
        User user = new User(email, login, password);

        registration(user, HttpStatus.OK);
        ResponseEntity<Status> statusResponseEntity = login(user, HttpStatus.OK);
        String cookie = statusResponseEntity.getHeaders().get("Set-Cookie").get(0);

        ResponseEntity<User> userResponseEntity = getMe(cookie);
        assertEquals(HttpStatus.OK, userResponseEntity.getStatusCode());
        assertEquals(user.getEmail(), userResponseEntity.getBody().getEmail());
        assertEquals(user.getLogin(), userResponseEntity.getBody().getLogin());
    }

    @Test
    public void getUserByIdTest(){
        User user1 = new User(email, login, password);
        registration(user1, HttpStatus.OK);
        setFields();
        User user2 = new User(email, login, password);
        registration(user2, HttpStatus.OK);

        ResponseEntity<Status> statusResponseEntity1 = login(user1, HttpStatus.OK);
        String cookie1 = statusResponseEntity1.getHeaders().get("Set-Cookie").get(0);
        ResponseEntity<User> userResponseEntity1 = getMe(cookie1);
        logout(cookie1);

        ResponseEntity<Status> statusResponseEntity2 = login(user2, HttpStatus.OK);
        String cookie2 = statusResponseEntity2.getHeaders().get("Set-Cookie").get(0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie2);
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<UserView> userResponseEntity =
                restTemplate.exchange("/api/user/" + userResponseEntity1.getBody().getId(), HttpMethod.GET, request, UserView.class);
        assertEquals(user1.getLogin(), userResponseEntity.getBody().getLogin());

        logout(cookie2);
    }

    @Test
    public void changeUserTest(){
        User user = new User(email, login, password);
        registration(user, HttpStatus.OK);
        String cookie = login(user, HttpStatus.OK).getHeaders().get(HttpHeaders.SET_COOKIE).get(0);

        User userForUpdate;
        String newEmail = getRandomEmail();
        String newLogin = getRandomString(8);
        String newPassword = getRandomString(8);

        userForUpdate = new User(newEmail, null,null);
        updateUser(cookie, userForUpdate, HttpStatus.OK);

        userForUpdate = new User(null, newLogin,null);
        updateUser(cookie, userForUpdate, HttpStatus.OK);

        userForUpdate = new User(null, null, newPassword);
        updateUser(cookie, userForUpdate, HttpStatus.OK);
    }
}
