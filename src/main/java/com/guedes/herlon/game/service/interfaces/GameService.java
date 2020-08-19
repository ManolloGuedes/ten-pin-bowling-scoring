package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;

import java.io.IOException;
import java.util.List;

/**
 * Interface involving actions on the game.
 * @author herlon-guedes
 * @since 08/11/2020
 */
public interface GameService {
    /**
     * Uses a list of ThrowDetails to create a game
     * @param throwDetailsList list of ThrowDetails instances
     * @return Game instance
     */
    Game createGameUsing(List<ThrowDetails> throwDetailsList) throws IOException;

    /**
     * Calculates the game result
     * @param game instance of Game
     */
    void calculateFinalResultOf(Game game);

    /**
     * calculates a specific player's game score
     * @param player instance of Player to calculates the score
     */
    void calculateGameScore(Player player);
}
