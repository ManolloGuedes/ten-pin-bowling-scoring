package com.guedes.herlon.game.general.factory;

import com.guedes.herlon.game.model.PlayerThrowImpl;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;

/**
 * Factory for PlayerThrow beans.
 * @author herlon-guedes
 * @since 08/11/2020
 * @see com.guedes.herlon.game.model.interfaces.PlayerThrow
 */
public class PlayerThrowFactory {

    /**
     * Creates a PlayerThrow fault instance.
     * @param knockedDownPins number of knocked down pins
     * @return PlayerThrow instance with the spare and strike attributes set to false and the fault attribute is true
     */
    public static PlayerThrow createFaultInstance(long knockedDownPins) {
        return PlayerThrowImpl.builder()
                .knockedDownPins(knockedDownPins)
                .strike(false)
                .spare(false)
                .fault(true)
                .build();
    }

    /**
     * Creates a common PlayerThrow using all class attributes
     * @param knockedDownPins number of knocked down pins
     * @param strike boolean that defines whether PlayerThrow is a strike
     * @param spare boolean that defines whether PlayerThrow is a spare
     * @return PlayerThrow instance
     */
    public static PlayerThrow createCommonInstance(long knockedDownPins, boolean strike, boolean spare) {
        return PlayerThrowImpl.builder()
                .knockedDownPins(knockedDownPins)
                .strike(strike)
                .spare(spare)
                .fault(false)
                .build();
    }
}
