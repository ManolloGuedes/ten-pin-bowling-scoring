package com.guedes.herlon.game.service;

import com.guedes.herlon.game.exceptions.TooMuchFramesException;
import com.guedes.herlon.game.exceptions.TooMuchThrowsException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.factory.PlayerThrowFactory;
import com.guedes.herlon.game.model.FrameImpl;
import com.guedes.herlon.game.model.GameImpl;
import com.guedes.herlon.game.model.PlayerImpl;
import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import com.guedes.herlon.game.service.interfaces.FrameService;
import com.guedes.herlon.game.service.interfaces.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final FrameService frameService;

    private AtomicReference<Frame> referenceToCurrentFrame;
    private AtomicReference<Player> referenceToCurrentPlayer;

    @Autowired
    public GameServiceImpl(FrameService frameService) {
        this.frameService = frameService;
    }

    /**
     * Goes through the list of ThrowDetails and create the game Players, Frames and PlayerThrow instances
     * @param throwDetailsList list of ThrowDetails instances
     * @return game instance
     */
    @Override
    public Game createGameUsing(List<ThrowDetails> throwDetailsList) {
        this.referenceToCurrentFrame = new AtomicReference<>();
        this.referenceToCurrentPlayer = new AtomicReference<>();
        try {
            Game game = new GameImpl(new ArrayList<>());

            throwDetailsList.forEach(throwDetails -> {
                registerPlayerThrowInto(game, throwDetails);
            });

            return game;
        } catch (Exception e) {
            log.error("Error on createGameUsing execution");
            throw e;
        }
    }

    /**
     * Goes through the list of Players and calculate its final result
     * @param game instance of Game
     */
    @Override
    public void calculateFinalResultOf(Game game) {
        game.getPlayers().forEach(this::calculateGameScore);
    }

    /**
     * Calculates the player's score by going through each frame and calculating its result.
     * @param player instance of Player to calculates the score
     */
    @Override
    public void calculateGameScore(Player player) {
        List<Frame> playerFrames = player.getFrames();

        frameService.calculateFrameScore(playerFrames, playerFrames.size() - 1);
    }

    /**
     * Creates a player's throw using throwDetails and insert it into the game.
     * @param game instance of Game
     * @param throwDetails instance of ThrowDetails
     */
    private void registerPlayerThrowInto(Game game, ThrowDetails throwDetails) throws RuntimeException {
        boolean needsToChangeCurrentPlayerOrFrame = needsToChangeCurrentPlayerOrFrame(throwDetails);
        if(needsToChangeCurrentPlayerOrFrame) {
            changeCurrentPlayer(game, throwDetails.getPlayerName());
            registerFrame(throwDetails.getPlayerName());

            if(!game.hasPlayer(throwDetails.getPlayerName())) {
                game.getPlayers().add(referenceToCurrentPlayer.get());
            }
        }

        registerThrowOnCurrentFrame(throwDetails.getThrowResult());
    }

    /**
     * Checks if it is necessary to change the current player and frame by analyzing the number of throws in the frame
     * and if the throwDetails#playerName is the current player.
     * @param throwDetails instance of ThrowDetails
     * @return boolean
     */
    private boolean needsToChangeCurrentPlayerOrFrame(ThrowDetails throwDetails) {
        Player currentPlayer = referenceToCurrentPlayer.get();
        Frame currentFrame = referenceToCurrentFrame.get();
        boolean needsToChangeCurrentPlayer = currentPlayer == null || !currentPlayer.getName().equals(throwDetails.getPlayerName());

        boolean needsToChangeCurrentFrame = true;

        if(currentFrame != null && currentPlayer != null) {
            boolean currentFrameHasMaxNumberOfThrows = currentFrame.getPlayerThrowList().size() == Constants.NON_LAST_FRAME_MAX_NUMBER_THROWS;
            boolean allPinsAreDown = currentFrame.getTotalKnockedDownPins() == Constants.MAX_NUMBER_OF_PINS;
            boolean allFramesDone = currentPlayer.getFrames().size() == Constants.MAX_NUMBER_OF_FRAMES;

            needsToChangeCurrentFrame = !allFramesDone && (currentFrameHasMaxNumberOfThrows || allPinsAreDown);
        }

        return needsToChangeCurrentPlayer || needsToChangeCurrentFrame;
    }

    /**
     * Register a new frame to current player into referenceToCurrentFrame
     * @param playerName frame player's name
     * @throws TooMuchFramesException when we try to insert more than Constants#MAX_NUMBER_OF_FRAMES into the game
     * @see Constants#MAX_NUMBER_OF_FRAMES
     */
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

    /**
     * Changes the reference in referenceToCurrentPlayer to the player whose name is defined by playerName.
     * If there is no player using this name, a new Player instance will be created.
     * @param game instance of Game
     * @param playerName player's name
     */
    private void changeCurrentPlayer(Game game, String playerName) {
        referenceToCurrentPlayer.set(game.getPlayers()
                .stream()
                .filter(currentPlayer -> currentPlayer.getName().equals(playerName))
                .findFirst()
                .orElseGet(() -> new PlayerImpl(playerName, new ArrayList<>())));
    }

    /**
     * Create a new Throw using the result in throwResult and register it in the referenceToCurrentFrame.
     * @param throwResult throw result
     * @throws TooMuchThrowsException when we try to insert more than Constants#MAX_NUMBER_OF_PINS into the frame
     * @see Constants#MAX_NUMBER_OF_PINS
     */
    private void registerThrowOnCurrentFrame(String throwResult) throws TooMuchThrowsException {
        List<PlayerThrow> playerThrows = referenceToCurrentFrame.get().getPlayerThrowList();
        List<Frame> playerFrames = referenceToCurrentPlayer.get().getFrames();

        PlayerThrow playerThrow;
        boolean isFault = !NumberUtils.isCreatable(throwResult);
        if(isFault) {
            playerThrow = PlayerThrowFactory.createFaultInstance(Constants.FAULT_NUMBER_KNOCKED_DOWN_PINS);
        } else {
            validIfCanCreateThrow(playerThrows, playerFrames, throwResult);

            long knockedDownPins = Long.parseLong(throwResult);
            boolean isLastFrame = playerFrames.size() == Constants.MAX_NUMBER_OF_FRAMES;
            boolean strike = knockedDownPins == Constants.MAX_NUMBER_OF_PINS &&
                    (isLastFrame || referenceToCurrentFrame.get().getPlayerThrowList().isEmpty());
            long totalKnockedDownPins = referenceToCurrentFrame.get().getTotalKnockedDownPins() + knockedDownPins;
            boolean spare = !strike && totalKnockedDownPins == Constants.MAX_NUMBER_OF_PINS;
            playerThrow = PlayerThrowFactory.createCommonInstance(knockedDownPins, strike, spare);
        }

        playerThrows.add(playerThrow);
    }

    /**
     * Checks if it's possible to create a new PlayerThrow.
     * If the limit of throws and the sum of knocked down pins in actual frame have not been exceeded, we can create a new PlayerThrow.
     * @param playerThrows list of PlayerThrow instances
     * @param playerFrames list of Frame instances
     * @param throwResult result of the Throw that we are trying to create
     * @throws TooMuchThrowsException occurs when validation fails
     */
    private void validIfCanCreateThrow(List<PlayerThrow> playerThrows, List<Frame> playerFrames, String throwResult) throws TooMuchThrowsException {
        boolean canThrowInCurrentFrame = !((playerFrames.size() < Constants.MAX_NUMBER_OF_FRAMES &&
                                        playerThrows.size() >= Constants.NON_LAST_FRAME_MAX_NUMBER_THROWS)
                                        || (playerFrames.size() == Constants.MAX_NUMBER_OF_FRAMES
                                        && playerThrows.size() >= Constants.LAST_FRAME_MAX_NUMBER_THROWS));
        if (!canThrowInCurrentFrame) {
            String errorMessage = String.format("Error on registerThrowOnCurrentFrame execution. Number of throws in one frame exceeded by player %s on the Frame #%d",
                    referenceToCurrentPlayer.get().getName(), playerFrames.size());
            throw new TooMuchThrowsException(errorMessage);
        }

        long knockedDownPins = Long.parseLong(throwResult);
        long totalKnockedDownPins = referenceToCurrentFrame.get().getTotalKnockedDownPins() + knockedDownPins;

        boolean totalKnockedDownExceedsLimit = playerFrames.size() < Constants.MAX_NUMBER_OF_FRAMES && totalKnockedDownPins > Constants.MAX_NUMBER_OF_PINS;
        if (totalKnockedDownExceedsLimit) {
            String errorMessage = String.format("Error on registerThrowOnCurrentFrame execution. Number of knocked down pins by player %s on the Frame #%d exceeded the limit",
                    referenceToCurrentPlayer.get().getName(), playerFrames.size());
            throw new TooMuchThrowsException(errorMessage);
        }
    }

}
