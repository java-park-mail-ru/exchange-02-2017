package com.cyclic;

import com.cyclic.configs.Enums;
import com.cyclic.configs.RoomConfig;
import com.cyclic.models.game.Player;
import com.cyclic.models.game.Room;
import com.cyclic.models.game.net.toclient.ConnectionError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by serych on 27.04.17.
 */
@RunWith(SpringRunner.class)
public class GameProcessTest {

    private RoomConfig roomConfig;

    public GameProcessTest() {
        roomConfig = new RoomConfig();
        roomConfig.setStartTowerUnits(100);
        roomConfig.setBonusMaxValue(100);
        roomConfig.setBonusMinValue(10);
        roomConfig.setFieldHeight(20);
        roomConfig.setFieldWidth(20);
        roomConfig.setPlayersCount(2);
        roomConfig.setStartBonusCount(20);
    }


    @Test
    public void testEmptyRoom() {
        Room room = new Room(0, null, roomConfig);
        assertEquals(true, room.isEmpty());
        assertEquals(false, room.isFull());
    }

    @Test
    public void testNotPlayingRoom() {
        Room room = new Room(0, null, roomConfig);
        Player player1 = new Player(new TestWebSocketSession(message -> {
            Gson gson = new GsonBuilder().create();
            Room qwe = gson.fromJson(message, Room.class);
            assertEquals(room.getRoomID(), qwe.getRoomID());
        }), "Lalke", 0);
        room.addPlayer(player1);
    }

    @Test
    public void testGameplayKillNode() {
        Room room = new Room(0, null, roomConfig);
        Player player1 = createTestPlayer("Lalke", 0);
        room.addPlayer(player1);
        assertEquals(1, room.getPlayersCount());
        Player player2 = createTestPlayer("Kopalke", 1);
        room.addPlayer(player2);
        assertEquals(2, room.getPlayersCount());
    }

    private Player createTestPlayer(String userName, int id) {
        Player player = new Player(null, userName, id);
        WebSocketSession webSocketSession = new TestWebSocketSession(message -> {
            Gson gson = new GsonBuilder().create();
            Room room = gson.fromJson(message, Room.class);
            if (room != null) {
                //player.sendString("{\"action\":\"ACTION_GIVE_ME_ROOM\"");
                // Make first move
                if (room.getStatus() == Enums.RoomStatus.STATUS_PLAYING && room.getPid() == player.getId()) {
                    player.sendString("{\"move\":{\"xfrom\":3,\"yfrom\":19,\"xto\":4,\"yto\":18,\"unitsCount\":50},\"action\":\"ACTION_GAME_MOVE\"}");
                }
            }
            ConnectionError connectionError = gson.fromJson(message, ConnectionError.class);
            if (connectionError != null) {
                System.out.println(connectionError.getReason());
                assertNotEquals(null, connectionError);
            }
        });
        player.setWebSocketSession(webSocketSession);
        return player;
    }

    interface MessageWait {
        void callback(String message);
    }

    class TestWebSocketSession implements WebSocketSession {

        MessageWait messageWait;

        public TestWebSocketSession(MessageWait runnable) {
            this.messageWait = runnable;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public URI getUri() {
            return null;
        }

        @Override
        public HttpHeaders getHandshakeHeaders() {
            return null;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return null;
        }

        @Override
        public Principal getPrincipal() {
            return null;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getAcceptedProtocol() {
            return null;
        }

        @Override
        public int getTextMessageSizeLimit() {
            return 0;
        }

        @Override
        public void setTextMessageSizeLimit(int i) {

        }

        @Override
        public int getBinaryMessageSizeLimit() {
            return 0;
        }

        @Override
        public void setBinaryMessageSizeLimit(int i) {

        }

        @Override
        public List<WebSocketExtension> getExtensions() {
            return null;
        }

        @Override
        public void sendMessage(WebSocketMessage<?> webSocketMessage) throws IOException {
            messageWait.callback(webSocketMessage.getPayload().toString());
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void close(CloseStatus closeStatus) throws IOException {

        }
    }
}
