package com.cyclic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.cyclic.models.game.Room.PLAYERS_COUNT;

/**
 * Created by algys on 08.02.17.
 */


@SuppressWarnings({"DefaultFileTemplate", "SpringFacetCodeInspection", "WeakerAccess"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        if (!validateConstants())
            throw new Exception("Please check constants before starting the server!");
        SpringApplication.run(Application.class, args);
    }

    private static boolean validateConstants() {
        if (PLAYERS_COUNT <= 1)
            return false;
        return true;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


