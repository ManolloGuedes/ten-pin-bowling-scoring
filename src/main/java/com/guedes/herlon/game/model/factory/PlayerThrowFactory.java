package com.guedes.herlon.game.model.factory;

import com.guedes.herlon.game.model.PlayerThrowImpl;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;

public class PlayerThrowFactory {

    public static PlayerThrow createFaultInstance(long knockedDownPins) {
        return PlayerThrowImpl.builder()
                .knockedDownPins(knockedDownPins)
                .strike(false)
                .spare(false)
                .fault(true)
                .build();
    }

    public static PlayerThrow createCommonInstance(long knockedDownPins, boolean strike, boolean spare) {
        return PlayerThrowImpl.builder()
                .knockedDownPins(knockedDownPins)
                .strike(strike)
                .spare(spare)
                .fault(false)
                .build();
    }
}
