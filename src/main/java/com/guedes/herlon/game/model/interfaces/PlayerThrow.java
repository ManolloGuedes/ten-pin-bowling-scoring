package com.guedes.herlon.game.model.interfaces;

public interface PlayerThrow {
    /**
     * Gets the number of knocked down pins
     * @return number of knocked down pins
     */
    Long getKnockedDownPins();

    /**
     * Checks if the throw is a strike
     * @return true if it is a strike, otherwise false
     */
    Boolean getStrike();

    /**
     * Checks if the throw is a spare
     * @return true if it is a spare, otherwise false
     */
    Boolean getSpare();

    /**
     * Checks if the throw is a fault
     * @return true if it is a fault, otherwise false
     */
    Boolean getFault();
}
