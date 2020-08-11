package com.guedes.herlon.game.service;

import com.guedes.herlon.game.exceptions.TooMuchFramesException;
import com.guedes.herlon.game.exceptions.TooMuchThrowsException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.enums.BonusRule;
import com.guedes.herlon.game.model.*;
import com.guedes.herlon.game.general.utils.FileUtils;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.GameService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
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

        lines.forEach(line -> {
            try {
                registerPlayerThrow(game, playerName, line);
            } catch (Exception e) {
                log.error("Error on createGameUsing execution. Error while reading file line from file " + fileName, e);
            }
        });

        return game;
    }

    @Override
    public void calculateFinalResultOf(Game game) {
        game.getPlayers().forEach(this::calculateGameScore);
    }

    @Override
    public void calculateGameScore(Player player) {
        List<Frame> playerFrames = player.getFrames();
        calculateFrameScore(playerFrames, playerFrames.size() - 1);
    }

    @Override
    public void printFormattedScoreOf(Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        game.getPlayers().forEach(player -> {
            stringBuilder
                    .append(player.getName())
                    .append("\n")
                    .append("Pinfalls\t")
                    .append(this.getThrows(player.getFrames())
                            .stream()
                            .map(playerThrow -> {
                                if(playerThrow.getStrike()) {
                                    return "\t".concat(Constants.STRIKE_CHARACTER);
                                } else if (playerThrow.getSpare()) {
                                    return Constants.SPARE_CHARACTER;
                                } else if (playerThrow.getFault()) {
                                    return Constants.FAULT_CHARACTER;
                                }
                                return playerThrow.getKnockedDownPins().toString();
                            })
                            .collect(Collectors.joining("\t"))
                    ).append("\n")
                    .append("Score\t\t")
                    .append(player.getFrames()
                            .stream()
                            .map(frame -> frame.getScore().toString())
                            .collect(Collectors.joining("\t\t"))
                    ).append("\n");
        });

        System.out.println(stringBuilder.toString());
    }

    private void registerPlayerThrow(Game game, String[] playerName, String fileLine) throws RuntimeException {
        String[] throwDetails = fileLine.split(Constants.FILE_LINE_ELEMENT_SPLITTER);

        if(!playerName[0].equals(throwDetails[0])) {
            registerFrame(game, playerName, throwDetails);
        }

        registerThrow(throwDetails[1]);
    }

    private void registerFrame(Game game, String[] playerName, String[] throwDetails) throws TooMuchFramesException {
        playerName[0] = throwDetails[0];

        playerAtomicReference.set(game.getPlayers()
                .stream()
                .filter(actualPlayer -> actualPlayer.getName().equals(playerName[0]))
                .findFirst()
                .orElseGet(() -> new PlayerImpl(playerName[0], new ArrayList<>())));

        if(playerAtomicReference.get().getFrames().size() >= Constants.MAX_NUMBER_OF_FRAMES) {
            String errorMessage = String.format("Error on registerFrame execution. %s exceeded the maximum number of frames.",
                    playerName[0]);

            log.error(errorMessage);
            throw new TooMuchFramesException(errorMessage);
        }

        frameAtomicReference.set(new FrameImpl(new ArrayList<>(), (long) playerAtomicReference.get().getFrames().size()));
        playerAtomicReference.get().getFrames().add(frameAtomicReference.get());

        if(!game.hasPlayer(playerName[0])) {
            game.getPlayers().add(playerAtomicReference.get());
        }
    }

    private void registerThrow(String throwResult) throws TooMuchThrowsException {
        PlayerThrow playerThrow;
        boolean isFault = !NumberUtils.isCreatable(throwResult);
        if(isFault) {
            playerThrow = getThrowFault();
        } else {
            playerThrow = getThrowUsing(throwResult);
        }

        List<PlayerThrow> playerThrows = frameAtomicReference.get().getPlayerThrowList();
        List<Frame> playerFrames = playerAtomicReference.get().getFrames();
        if ((playerFrames.size() < Constants.MAX_NUMBER_OF_FRAMES && playerThrows.size() >= Constants.NON_LAST_FRAME_MAX_NUMBER_THROWS)
        || playerFrames.size() == Constants.MAX_NUMBER_OF_FRAMES && playerThrows.size() >= Constants.LAST_FRAME_MAX_NUMBER_THROWS) {
            String errorMessage = String.format("Error on registerThrow execution. Number of throws in one frame exceeded by player %s on the Frame #%d",
                                                playerAtomicReference.get().getName(), playerFrames.size());
            throw new TooMuchThrowsException(errorMessage);
        }

        playerThrows.add(playerThrow);
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
                                .spare(!strike && frameAtomicReference.get().getTotalKnockedDownPins() + knockedDownPins == Constants.MAX_NUMBER_OF_PINS)
                                .fault(false)
                                .build();
        return playerThrow;
    }

    private PlayerThrow getThrowFault() {
        PlayerThrow playerThrow;
        playerThrow = PlayerThrowImpl.builder()
                                .knockedDownPins(Constants.FAULT_NUMBER_KNOCKED_DOWN_PINS)
                                .strike(false)
                                .spare(false)
                                .fault(true)
                                .build();
        return playerThrow;
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
