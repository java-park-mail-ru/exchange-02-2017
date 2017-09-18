package com.cyclic.services;

import com.cyclic.models.base.ScoreBoard;
import com.cyclic.models.base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by algys on 24.03.17.
 */


@SuppressWarnings({"DefaultFileTemplate", "StringBufferReplaceableByString"})
@Service
@Transactional
public class AccountService {

    public static final int HIGHSCORE_FOR_PAGE = 10;
    public static final int ERROR_DUPLICATE = 1;
    public static final int OK = 0;
    private static final int ERROR_UNDEFINED = 2;

    final private JdbcTemplate template;
    private final RowMapper<User> userMapper = (rs, num) -> {
        final long id = rs.getInt("id");
        final long highScore = rs.getLong("highScore");
        final String login = rs.getString("login");
        final String email = rs.getString("email");
        final String password = rs.getString("password");
        final String firstName = rs.getString("firstName");
        final String lastName = rs.getString("lastName");
        return new User(id, firstName, lastName, email, login, password, highScore);
    };

    @Autowired
    AccountService(JdbcTemplate template) {
        this.template = template;
    }

    public int addUser(User newUser) {
        String query = new StringBuilder()
                .append("INSERT INTO users (login, email, password, firstName, lastName) ")
                .append("VALUES(?,?,?,?,?) ;")
                .toString();
        try {
            template.update(query, newUser.getLogin(), newUser.getEmail(),
                    newUser.getPassword(), newUser.getFirstName(), newUser.getLastName());
        } catch (DuplicateKeyException e) {
            return ERROR_DUPLICATE;
        }
        return OK;
    }

    public User getUserById(Long id) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE id = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int updateUserHighscore(String nickname, long score) {
        String query = new StringBuilder()
                .append("UPDATE users SET highScore = ? ")
                .append("WHERE login = ? ;")
                .toString();
        try {
            template.update(query, score, nickname);
        } catch (DuplicateKeyException e) {
            return ERROR_DUPLICATE;
        } catch (DataAccessException e) {
            return ERROR_UNDEFINED;
        }
        return OK;
    }

    public ScoreBoard getScoreBoard(long page) {

        String query = new StringBuilder()
                .append("SELECT * FROM users ORDER BY highScore DESC LIMIT ")
                .append(HIGHSCORE_FOR_PAGE)
                .append(" OFFSET ")
                .append(HIGHSCORE_FOR_PAGE * (page - 1))
                .toString();

        try {
            List<User> users = template.query(query, userMapper);
            return new ScoreBoard(users, page);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByLogin(String login) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE login = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, login);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE email = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, email);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int setUser(User updatedUser) {
        String query = new StringBuilder()
                .append("UPDATE users SET login = ?, email = ?, password = ?, firstName = ?, lastName = ? ")
                .append("WHERE id = ? ;")
                .toString();
        try {
            template.update(query, updatedUser.getLogin(), updatedUser.getEmail(),
                    updatedUser.getPassword(), updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getId());
        } catch (DuplicateKeyException e) {
            return ERROR_DUPLICATE;
        } catch (DataAccessException e) {
            return ERROR_UNDEFINED;
        }
        return OK;
    }

    public List<User> getUsers(int offset, int limit) {
        String query = new StringBuilder()
                .append("SELECT * FROM users ")
                .append("ORDER BY id ")
                .append("LIMIT ? ")
                .append("OFFSET ? ;")
                .toString();

        List<Map<String, Object>> rows;
        try {
            rows = template.queryForList(query, limit, offset);
        } catch (DataAccessException e) {
            return null;
        }
        List<User> users = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            String firstName = null;
            if (row.get("firstName") != null)
                firstName = row.get("firstName").toString();
            String lastName = null;
            if (row.get("lastName") != null)
                lastName = row.get("lastName").toString();
            users.add(new User(
                            Long.parseLong(row.get("id").toString()), firstName,
                            lastName, row.get("email").toString(),
                            row.get("login").toString(), row.get("password").toString(),
                            Long.parseLong(row.get("highScore").toString())
                    )
            );
        }
        return users;
    }

}
