package com.guedes.herlon.game.service;

import com.guedes.herlon.game.model.Frame;
import com.guedes.herlon.game.model.Game;
import com.guedes.herlon.game.model.Player;
import com.guedes.herlon.game.model.PlayerThrow;
import com.guedes.herlon.game.general.utils.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GameServiceImpl implements GameService {
    private final AtomicReference<Frame> frameAtomicReference;
    private final AtomicReference<Player> playerAtomicReference;

    public GameServiceImpl() {
        frameAtomicReference = new AtomicReference<>();
        playerAtomicReference = new AtomicReference<>();
    }

    @Override
    public Game createGameUsing(String fileName) {
        Game game = new Game(new ArrayList<>());

        List<String> lines = FileUtils.getLinesFromFile(fileName);

        final String[] playerName = {""};

        lines.forEach(line -> registerPlayerThrow(game, playerName, line));

        print(game);

        return game;
    }

    @Override
    public void calculateFinalResultOf(Game game) {
        game.getPlayers().forEach(Player::calculateGameScore);
    }

    private void print(Game game) {
        System.out.println(game.getPlayers().size());

        game.getPlayers().forEach(player -> {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("-------------------");
            System.out.println(player.getName());
            System.out.println("-------------------");
            player.getFrames().forEach(frame -> {
                System.out.println("Frame: ".concat(frame.getNumber().toString()));
                frame.getPlayerThrowList().forEach(playerThrow -> System.out.println(playerThrow.getFault() ? "F" : playerThrow.getStrike() ? "X" :
                        playerThrow.getSpare() ? "/" : playerThrow.getKnockedDownPins().toString()));
            });
        });
    }

    private void registerPlayerThrow(Game game, String[] playerName, String fileLine) {
        String[] throwDetails = fileLine.split("\t");

        if(!playerName[0].equals(throwDetails[0])) {
            playerName[0] = throwDetails[0];

            playerAtomicReference.set(game.getPlayers()
                    .stream()
                    .filter(actualPlayer -> actualPlayer.getName().equals(playerName[0]))
                    .findFirst()
                    .orElseGet(() -> new Player(playerName[0], new ArrayList<>())));

            frameAtomicReference.set(new Frame(new ArrayList<>(), (long) playerAtomicReference.get().getFrames().size()));
            playerAtomicReference.get().getFrames().add(frameAtomicReference.get());

            if(!game.hasPlayer(playerName[0])) {
                game.getPlayers().add(playerAtomicReference.get());
            }
        }

        registerThrow(throwDetails[1]);
    }

    private void registerThrow(String throwResult) {
        PlayerThrow playerThrow;
        boolean isFault = !NumberUtils.isCreatable(throwResult);
        if(isFault) {
            playerThrow = getThrowFault();
        } else {
            playerThrow = getThrowUsing(throwResult);
        }

        frameAtomicReference.get().getPlayerThrowList().add(playerThrow);
    }

    private PlayerThrow getThrowUsing(String throwResult) {
        PlayerThrow playerThrow;
        Long knockedDownPins = Long.parseLong(throwResult);
        boolean strike = false;
        if(knockedDownPins == 10) {
            strike = frameAtomicReference.get().getPlayerThrowList().isEmpty();
        }
        playerThrow = PlayerThrow.builder()
                                .knockedDownPins(knockedDownPins)
                                .strike(strike)
                                .spare(!strike && frameAtomicReference.get().getTotalKnockedDownPins() + knockedDownPins == 10)
                                .fault(false)
                                .build();
        return playerThrow;
    }

    private PlayerThrow getThrowFault() {
        PlayerThrow playerThrow;
        playerThrow = PlayerThrow.builder()
                                .knockedDownPins(0L)
                                .strike(false)
                                .spare(false)
                                .fault(true)
                                .build();
        return playerThrow;
    }
}
