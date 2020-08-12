package com.guedes.herlon.game.service;

import com.guedes.herlon.game.exceptions.TooMuchFramesException;
import com.guedes.herlon.game.exceptions.TooMuchThrowsException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.factory.PlayerThrowFactory;
import com.guedes.herlon.game.general.utils.FileUtils;
import com.guedes.herlon.game.model.FrameImpl;
import com.guedes.herlon.game.model.GameImpl;
import com.guedes.herlon.game.model.PlayerImpl;
import com.guedes.herlon.game.model.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.FrameService;
import com.guedes.herlon.game.service.interfaces.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final AtomicReference<Frame> referenceToCurrentFrame;
    private final AtomicReference<Player> referenceToCurrentPlayer;

    public GameServiceImpl() {
        referenceToCurrentFrame = new AtomicReference<>();
        referenceToCurrentPlayer = new AtomicReference<>();
    }

    @Override
    public Game createGameUsing(String fileName) throws IOException {
        Game game = new GameImpl(new ArrayList<>());

        List<String> lines = FileUtils.getLinesFromFile(fileName);

        lines.forEach(line -> {
            try {
                ThrowDetails throwDetails = ThrowDetails.recoverThrowDetailsFrom(line, Constants.FILE_LINE_ELEMENT_SPLITTER);
                registerPlayerThrowInto(game, throwDetails);
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

    private void registerPlayerThrowInto(Game game, ThrowDetails throwDetails) throws RuntimeException {

        Player currentPlayer = referenceToCurrentPlayer.get();
        boolean needsToChangeCurrentPlayer = currentPlayer == null || !currentPlayer.getName().equals(throwDetails.getPlayerName());
        if(needsToChangeCurrentPlayer) {
            changeCurrentPlayer(game, throwDetails.getPlayerName());
            registerFrame(throwDetails.getPlayerName());

            if(!game.hasPlayer(throwDetails.getPlayerName())) {
                game.getPlayers().add(referenceToCurrentPlayer.get());
            }
        }

        registerThrowOnCurrentFrame(throwDetails.getThrowResult());
    }

    private void registerFrame(String playerName) throws TooMuchFramesException {
        if(referenceToCurrentPlayer.get().getFrames().size() >= Constants.MAX_NUMBER_OF_FRAMES) {
            String errorMessage = String.format("Error on registerFrame execution. %s exceeded the maximum number of frames.",
                    playerName);

            log.error(errorMessage);
            throw new TooMuchFramesException(errorMessage);
        }

        referenceToCurrentFrame.set(new FrameImpl(new ArrayList<>(), (long) referenceToCurrentPlayer.get().getFrames().size()));
        referenceToCurrentPlayer.get().getFrames().add(referenceToCurrentFrame.get());
    }

    private void changeCurrentPlayer(Game game, String playerName) {
        referenceToCurrentPlayer.set(game.getPlayers()
                .stream()
                .filter(currentPlayer -> currentPlayer.getName().equals(playerName))
                .findFirst()
                .orElseGet(() -> new PlayerImpl(playerName, new ArrayList<>())));
    }

    private void registerThrowOnCurrentFrame(String throwResult) throws TooMuchThrowsException {
        List<PlayerThrow> playerThrows = referenceToCurrentFrame.get().getPlayerThrowList();
        List<Frame> playerFrames = referenceToCurrentPlayer.get().getFrames();
        boolean canThrowInCurrentFrame = (playerFrames.size() < Constants.MAX_NUMBER_OF_FRAMES &&
                                        playerThrows.size() >= Constants.NON_LAST_FRAME_MAX_NUMBER_THROWS)
                                        || (playerFrames.size() == Constants.MAX_NUMBER_OF_FRAMES
                                        && playerThrows.size() >= Constants.LAST_FRAME_MAX_NUMBER_THROWS);
        if (canThrowInCurrentFrame) {
            String errorMessage = String.format("Error on registerThrowOnCurrentFrame execution. Number of throws in one frame exceeded by player %s on the Frame #%d",
                    referenceToCurrentPlayer.get().getName(), playerFrames.size());
            throw new TooMuchThrowsException(errorMessage);
        }

        PlayerThrow playerThrow;
        boolean isFault = !NumberUtils.isCreatable(throwResult);
        if(isFault) {
            playerThrow = PlayerThrowFactory.createFaultInstance(Constants.FAULT_NUMBER_KNOCKED_DOWN_PINS);
        } else {
            long knockedDownPins = Long.parseLong(throwResult);
            boolean strike = knockedDownPins == Constants.MAX_NUMBER_OF_PINS && referenceToCurrentFrame.get().getPlayerThrowList().isEmpty();
            boolean spare = !strike && referenceToCurrentFrame.get().getTotalKnockedDownPins() + knockedDownPins == Constants.MAX_NUMBER_OF_PINS;
            playerThrow = PlayerThrowFactory.createCommonInstance(knockedDownPins, strike, spare);
        }

        playerThrows.add(playerThrow);
    }

}
