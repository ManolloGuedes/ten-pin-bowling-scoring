package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Game {
    /**
     * Checks for a player with a specific name in the game.
     * @param name name of the player being searched for
     * @return boolean indicating the player existence
     */
    Boolean hasPlayer(String name);

    /**
     * Gets the list of players in the game
     * @return List of Player instances
     */
    List<Player> getPlayers();
}
