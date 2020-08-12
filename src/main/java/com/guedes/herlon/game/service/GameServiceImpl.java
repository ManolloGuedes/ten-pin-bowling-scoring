package com.guedes.herlon.game.service;

import com.guedes.herlon.game.exceptions.TooMuchFramesException;
import com.guedes.herlon.game.exceptions.TooMuchThrowsException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.factory.PlayerThrowFactory;
import com.guedes.herlon.game.general.utils.FileUtils;
import com.guedes.herlon.game.model.FrameImpl;
import com.guedes.herlon.game.model.GameImpl;
import com.guedes.herlon.game.model.PlayerImpl;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.FrameService;
import com.guedes.herlon.game.service.interfaces.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final AtomicReference<Frame> refereceToCurrentFrame;
    private final AtomicReference<Player> referenceToCurrentPlayer;

    public GameServiceImpl() {
        refereceToCurrentFrame = new AtomicReference<>();
        referenceToCurrentPlayer = new AtomicReference<>();
    }

    @Override
    public Game createGameUsing(String fileName) {
        Game game = new GameImpl(new ArrayList<>());

        List<String> lines = FileUtils.getLinesFromFile(fileName);

        lines.forEach(line -> {
            try {
                registerPlayerThrowByFileLine(game, line);
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

        FrameService frameService = new FrameServiceImpl();
        frameService.calculateFrameScore(playerFrames, playerFrames.size() - 1);
    }

    @Override
    public void printFormattedScoreOf(Game game) {
        StringBuilder stringBuilder = new StringBuilder();

        FrameService frameService = new FrameServiceImpl();
        game.getPlayers().forEach(player -> {
            stringBuilder
                    .append(player.getName())
                    .append("\n")
                    .append("Pinfalls\t")
                    .append(frameService.getThrowsFrom(player.getFrames())
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

    private void registerPlayerThrowByFileLine(Game game, String fileLine) throws RuntimeException {
        String[] throwDetails = fileLine.split(Constants.FILE_LINE_ELEMENT_SPLITTER);

        Player currentPlayer = referenceToCurrentPlayer.get();
        boolean needsToChangeCurrentPlayer = currentPlayer == null || !currentPlayer.getName().equals(throwDetails[0]);
        if(needsToChangeCurrentPlayer) {
            changeCurrentPlayer(game, throwDetails[0]);
            registerFrame(game, throwDetails[0]);
        }

        registerThrowOnCurrentFrame(throwDetails[1]);
    }

    private void registerFrame(Game game, String playerName) throws TooMuchFramesException {
        if(referenceToCurrentPlayer.get().getFrames().size() >= Constants.MAX_NUMBER_OF_FRAMES) {
            String errorMessage = String.format("Error on registerFrame execution. %s exceeded the maximum number of frames.",
                    playerName);

            log.error(errorMessage);
            throw new TooMuchFramesException(errorMessage);
        }

        refereceToCurrentFrame.set(new FrameImpl(new ArrayList<>(), (long) referenceToCurrentPlayer.get().getFrames().size()));
        referenceToCurrentPlayer.get().getFrames().add(refereceToCurrentFrame.get());

        if(!game.hasPlayer(playerName)) {
            game.getPlayers().add(referenceToCurrentPlayer.get());
        }
    }

    private void changeCurrentPlayer(Game game, String playerName) {
        referenceToCurrentPlayer.set(game.getPlayers()
                .stream()
                .filter(currentPlayer -> currentPlayer.getName().equals(playerName))
                .findFirst()
                .orElseGet(() -> new PlayerImpl(playerName, new ArrayList<>())));
    }

    private void registerThrowOnCurrentFrame(String throwResult) throws TooMuchThrowsException {
        PlayerThrow playerThrow;
        boolean isFault = !NumberUtils.isCreatable(throwResult);
        if(isFault) {
            playerThrow = PlayerThrowFactory.createFaultInstance(Constants.FAULT_NUMBER_KNOCKED_DOWN_PINS);
        } else {
            long knockedDownPins = Long.parseLong(throwResult);
            boolean strike = knockedDownPins == Constants.MAX_NUMBER_OF_PINS && refereceToCurrentFrame.get().getPlayerThrowList().isEmpty();
            boolean spare = !strike && refereceToCurrentFrame.get().getTotalKnockedDownPins() + knockedDownPins == Constants.MAX_NUMBER_OF_PINS;
            playerThrow = PlayerThrowFactory.createCommonInstance(knockedDownPins, strike, spare);
        }

        List<PlayerThrow> playerThrows = refereceToCurrentFrame.get().getPlayerThrowList();
        List<Frame> playerFrames = referenceToCurrentPlayer.get().getFrames();
        if ((playerFrames.size() < Constants.MAX_NUMBER_OF_FRAMES && playerThrows.size() >= Constants.NON_LAST_FRAME_MAX_NUMBER_THROWS)
        || playerFrames.size() == Constants.MAX_NUMBER_OF_FRAMES && playerThrows.size() >= Constants.LAST_FRAME_MAX_NUMBER_THROWS) {
            String errorMessage = String.format("Error on registerThrowOnCurrentFrame execution. Number of throws in one frame exceeded by player %s on the Frame #%d",
                                                referenceToCurrentPlayer.get().getName(), playerFrames.size());
            throw new TooMuchThrowsException(errorMessage);
        }

        playerThrows.add(playerThrow);
    }

}
