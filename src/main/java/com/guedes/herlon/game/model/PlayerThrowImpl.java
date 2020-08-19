package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import lombok.Builder;
import lombok.Getter;

/**
 * Implementation of the PlayerThrow interface using the Builder Pattern.
 * @author herlon-guedes
 * @since 08/10/2020
 */
@Getter
@Builder
public class PlayerThrowImpl implements PlayerThrow {

    private final Long knockedDownPins;
    private final Boolean strike;
    private final Boolean spare;
    private final Boolean fault;

}