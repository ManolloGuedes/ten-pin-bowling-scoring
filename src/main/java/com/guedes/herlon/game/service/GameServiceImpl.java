package com.guedes.herlon.game.service;

import com.guedes.herlon.game.general.enums.BonusRule;
import com.guedes.herlon.game.model.*;
import com.guedes.herlon.game.general.utils.FileUtils;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.GameService;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        Game game = new GameImpl(new ArrayList<>());

        List<String> lines = FileUtils.getLinesFromFile(fileName);

        final String[] playerName = {""};

        lines.forEach(line -> registerPlayerThrow(game, playerName, line));

        print(game);

        return game;
    }

    @Override
    public void calculateFinalResultOf(Game game) {
        game.getPlayers().forEach(this::calculateGameScore);
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
                    .orElseGet(() -> new PlayerImpl(playerName[0], new ArrayList<>())));

            frameAtomicReference.set(new FrameImpl(new ArrayList<>(), (long) playerAtomicReference.get().getFrames().size()));
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
        playerThrow = PlayerThrowImpl.builder()
                                .knockedDownPins(knockedDownPins)
                                .strike(strike)
                                .spare(!strike && frameAtomicReference.get().getTotalKnockedDownPins() + knockedDownPins == 10)
                                .fault(false)
                                .build();
        return playerThrow;
    }

    private PlayerThrow getThrowFault() {
        PlayerThrow playerThrow;
        playerThrow = PlayerThrowImpl.builder()
                                .knockedDownPins(0L)
                                .strike(false)
                                .spare(false)
                                .fault(true)
                                .build();
        return playerThrow;
    }

    public void calculateGameScore(Player player) {
        List<Frame> playerFrames = player.getFrames();
        calculateFrameScore(playerFrames, playerFrames.size() - 1);
    }

    private List<PlayerThrow> getThrows(List<Frame> frames) {
        return frames.stream().flatMap(frame -> frame.getPlayerThrowList().stream()).collect(Collectors.toList());
    }

    private long calculateFrameScore(List<Frame> frames, int frameNumber) {
        long frameScore = 0;
        if(frameNumber >= 0) {
            frameScore += calculateFrameScore(frames, frameNumber - 1);
            frameScore += frameNumber < frames.size() - 1 ? calculateFrameBonus(frames, frameNumber) : 0L;
            frameScore += frames.get(frameNumber).getTotalKnockedDownPins();
            frames.get(frameNumber).setScore(frameScore);
        }
        return frameScore;
    }

    private long calculateFrameBonus(List<Frame> frames, int frameNumber) {
        long bonus = 0L;
        if (frames.get(frameNumber).isStrike()) {
            bonus = calculateBonusUsing(BonusRule.STRIKE, frames, frameNumber);
        } else if (frames.get(frameNumber).isSpare()) {
            bonus = calculateBonusUsing(BonusRule.SPARE, frames, frameNumber);
        }
        return bonus;
    }

    private long calculateBonusUsing(BonusRule rule, List<Frame> frames, int frameNumber) {
        Long bonusValue = 0L;

        if(frameNumber < frames.size()-1) {
            List<PlayerThrow> playerThrows = getThrows(frames);

            @NonNull List<PlayerThrow> playerThrowOnFrame = frames.get(frameNumber).getPlayerThrowList();
            int indexOfLastThrow = playerThrows.indexOf(playerThrowOnFrame.get(playerThrowOnFrame.size()-1));

            int amountThrowsToUse = Math.min(playerThrows.size() - indexOfLastThrow, rule.getThrowsToUse());

            List<PlayerThrow> playerThrowsToUseAsBonuses = playerThrows.subList(indexOfLastThrow + 1, indexOfLastThrow + 1 + amountThrowsToUse);

            bonusValue += playerThrowsToUseAsBonuses.stream().mapToLong(PlayerThrow::getKnockedDownPins).sum();
        } else {
            bonusValue += frames.get(frameNumber).getTotalKnockedDownPins();
        }

        return bonusValue;
    }
}
