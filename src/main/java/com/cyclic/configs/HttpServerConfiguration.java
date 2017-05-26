package com.cyclic.configs;

/**
 * Created by serych on 19.05.17.
 */

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan("com.cyclic")
public class HttpServerConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://cyclicgame.herokuapp.com",
                        "http://cyclicgame.herokuapp.com",
                        "https://scaptaincap.asuscomm.com:5000",
                        "http://scaptaincap.asuscomm.com:5000",
                        "http://172.16.91.197:3000",
                        "http://172.16.84.245:3000",
                        "http://172.20.10.2:3000",
                        "http://172.20.10.14:3000",
                        "http://172.20.10.4:3000")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedHeaders("Content-Type", "Origin", "X-Requested-With", "Accept")
                .allowCredentials(true)
                .maxAge(3600);
    }
}