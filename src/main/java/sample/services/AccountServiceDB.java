package sample.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.models.User;

import java.util.List;

/**
 * Created by algys on 24.03.17.
 */


@Service
@Transactional
public class AccountServiceDB implements AccountService {

    final private JdbcTemplate template;

    @Autowired
    AccountServiceDB(JdbcTemplate template){
        this.template = template;
    }

    public void addUser(User newUser) {
        String query = new StringBuilder()
                .append("INSERT INTO users (login, email, password, firstName, lastName) ")
                .append("VALUES(?,?,?,?,?) ;")
                .toString();

        template.update(query, newUser.getLogin(), newUser.getEmail(),
                newUser.getPassword(), newUser.getFirstName(), newUser.getLastName());
    }

    public Boolean hasUser(String login) {
        String query = new StringBuilder()
                .append("SELECT COUNT(*) FROM users WHERE login = ? ;")
                .toString();

        return template.queryForObject(query, Integer.class, login) == 1;
    }

    public User getUserById(Long id) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE id = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, id);
        }
        catch (DataAccessException e){
            return null;
        }
    }

    public User getUserByLogin(String login) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE login = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, login);
        }
        catch (DataAccessException e){
            return null;
        }
    }

    public User getUserByEmail(String email) {
        String query = new StringBuilder()
                .append("SELECT * FROM users WHERE email = ? ;")
                .toString();

        try {
            return template.queryForObject(query, userMapper, email);
        }
        catch (DataAccessException e){
            return null;
        }
    }

    public void setUser(User updatedUser) {
        String query = new StringBuilder()
                .append("UPDATE users SET (login, email, password, firstName, lastName) ")
                .append("VALUES (?,?,?,?,?) ;")
                .toString();

        template.update(query, updatedUser.getLogin(), updatedUser.getEmail(),
                updatedUser.getPassword(), updatedUser.getFirstName(), updatedUser.getLastName());
    }

    public List<User> getUsers() {
        return null;
    }

    private final RowMapper<User> userMapper = (rs, num) -> {
        final long id = rs.getInt("id");
        final String login = rs.getString("login");
        final String email = rs.getString("email");
        final String password = rs.getString("password");
        final String firstName = rs.getString("firstName");
        final String lastName = rs.getString("lastName");
        return new User(id, firstName, lastName, email, login, password);
    };
}
