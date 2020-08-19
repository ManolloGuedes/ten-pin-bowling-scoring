package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Player {
    /**
     * Gets the player's name
     * @return player's name
     */
    String getName();

    /**
     * Gets the player's list of frames
     * @return List of Frame instances
     */
    List<Frame> getFrames();
}
