package com.guedes.herlon.game.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerThrow {

    private final Long knockedDownPins;
    private final Boolean strike;
    private final Boolean spare;
    private final Boolean fault;

}