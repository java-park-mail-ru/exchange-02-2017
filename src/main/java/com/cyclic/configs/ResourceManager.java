package com.cyclic.configs;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Created by serych on 27.04.17.
 */

@EnableConfigurationProperties({RoomConfig.class})
@Service
public class ResourceManager {
    private final RoomConfig roomConfig;

    public ResourceManager(RoomConfig roomConfig) throws Exception {
        this.roomConfig = roomConfig;
        if (!isCorrect())
            throw new Exception("Please check resources before starting the server!");
    }

    public RoomConfig getRoomConfig() {
        return roomConfig;
    }

    public boolean isCorrect() {
        return roomConfig.isCorrect();
    }
}
