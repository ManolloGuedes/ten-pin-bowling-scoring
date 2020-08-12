package com.guedes.herlon.game.service;

import com.guedes.herlon.game.model.PlayerImpl;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
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
    void init() throws IOException {
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
                () -> assertEquals(10, jeffFrames.size()),
                () -> assertEquals(10, johnFrames.size())
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

    @Test
    void calculateFinalResult() {
        gameService.calculateFinalResultOf(gameTest);

        List<Frame> jeffFrames = getFramesUsing("Jeff");
        List<Frame> johnFrames = getFramesUsing("John");

        assertAll(
                () -> assertEquals(167, jeffFrames.get(jeffFrames.size()-1).getScore()),
                () -> assertEquals(151, johnFrames.get(jeffFrames.size()-1).getScore())
        );
    }

    private List<Frame> getFramesUsing(String playerName) {
        return gameTest.getPlayers()
                .stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElse(new PlayerImpl("", new ArrayList<>()))
                .getFrames();
    }
}