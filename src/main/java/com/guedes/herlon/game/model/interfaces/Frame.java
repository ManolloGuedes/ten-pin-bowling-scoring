package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Frame {
    /**
     * Gets the sum of all knocked down pins.
     * @return total number of knocked down pins
     */
    Long getTotalKnockedDownPins();

    /**
     * Checks if the frame has a strike throw.
     * @return boolean
     */
    boolean isStrike();

    /**
     * Checks if the frame has a spare throw.
     * @return boolean
     */
    boolean isSpare();
    List<PlayerThrow> getPlayerThrowList();

    /**
     * Gets the frame number
     * @return Long
     */
    Long getNumber();

    /**
     * Gets the frame score
     * @return Long
     */
    Long getScore();

    /**
     * Sets the frame score
     * @param score value to set into score
     */
    void setScore(Long score);
}
