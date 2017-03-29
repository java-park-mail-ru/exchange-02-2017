package com.cyclic.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.cyclic.controllers.GamePlayerWebSocketHandler;
import com.cyclic.services.ConnectedSessionsService;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSocket
@ComponentScan
public class WebSocketServerConfiguration implements WebSocketConfigurer {

    private ConnectedSessionsService connectedSessionsService;

    @Autowired
    public WebSocketServerConfiguration(ConnectedSessionsService connectedSessionsService){
        this.connectedSessionsService = connectedSessionsService;
    }

    @Bean
    public WebSocketHandler myWebSocketHandler() {
        return new GamePlayerWebSocketHandler(connectedSessionsService);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/game").setAllowedOrigins("*");
    }
}