package com.guedes.herlon.game.service;

import com.guedes.herlon.game.model.Frame;
import com.guedes.herlon.game.model.Game;
import com.guedes.herlon.game.model.Player;
import com.guedes.herlon.game.model.PlayerThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceImplTest {

    @Autowired
    private GameService gameService;
    private Game gameTest;

    @BeforeEach
    void init(){
        Path filePath = Paths.get("src/test/resources/", "test.txt");
        gameTest = gameService.createGameUsing(filePath.toString());
    }

    @Test
    void creatingGame() {
        assertAll(
                () -> assertTrue(gameTest.hasPlayer("Jeff")),
                () -> assertTrue(gameTest.hasPlayer("John")),
                () -> assertEquals(2, gameTest.getPlayers().size())
        );
    }

    @Test
    void verifyFrameAmount() {
        List<Frame> jeffFrames = getFramesUsing("Jeff");

        List<Frame> johnFrames = getFramesUsing("John");

        assertAll(
                () -> assertEquals(2, jeffFrames.size()),
                () -> assertEquals(2, johnFrames.size())
        );
    }

    @Test
    void verifyStrikes() {
        List<Frame> jeffFrames = getFramesUsing("Jeff");

        List<Frame> johnFrames = getFramesUsing("John");

        assertAll(
                () -> assertEquals(1, jeffFrames.get(0).getPlayerThrowList().size()),
                () -> assertEquals(10, jeffFrames.get(0).getPlayerThrowList().get(0).getKnockedDownPins()),
                () -> assertTrue(jeffFrames.get(0).getPlayerThrowList().get(0).getStrike()),
                () -> assertFalse(jeffFrames.get(0).getPlayerThrowList().get(0).getFault()),
                () -> assertFalse(jeffFrames.get(0).getPlayerThrowList().get(0).getSpare())
        );

        assertAll(
                () -> assertEquals(10, johnFrames.get(0).getPlayerThrowList().stream().mapToLong(PlayerThrow::getKnockedDownPins).sum()),
                () -> assertFalse(johnFrames.get(0).getPlayerThrowList().stream().anyMatch(PlayerThrow::getStrike))
        );
    }

    @Test
    void verifySpares() {
        List<Frame> jeffFrames = getFramesUsing("Jeff");

        List<Frame> johnFrames = getFramesUsing("John");

        assertAll(
                () -> assertFalse(jeffFrames.get(0).getPlayerThrowList().size() > 1),
                () -> assertEquals(10, jeffFrames.get(0).getPlayerThrowList().stream().mapToLong(PlayerThrow::getKnockedDownPins).sum()),
                () -> assertFalse(jeffFrames.get(0).getPlayerThrowList().get(0).getSpare())
        );

        assertAll(
                () -> assertEquals(10, johnFrames.get(0).getPlayerThrowList().stream().mapToLong(PlayerThrow::getKnockedDownPins).sum()),
                () -> assertTrue(johnFrames.get(0).getPlayerThrowList().stream().anyMatch(PlayerThrow::getSpare)),
                () -> assertFalse(johnFrames.get(0).getPlayerThrowList().stream().anyMatch(PlayerThrow::getStrike))
        );
    }

    private List<Frame> getFramesUsing(String playerName) {
        return gameTest.getPlayers()
                .stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElse(new Player("", new ArrayList<>()))
                .getFrames();
    }
}