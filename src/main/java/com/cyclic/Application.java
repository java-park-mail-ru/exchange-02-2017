package com.cyclic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by algys on 08.02.17.
 */


@SuppressWarnings({"DefaultFileTemplate", "SpringFacetCodeInspection", "WeakerAccess"})
@SpringBootApplication
public class Application {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}


