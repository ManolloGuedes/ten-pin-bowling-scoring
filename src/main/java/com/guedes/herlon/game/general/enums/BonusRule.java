package com.guedes.herlon.game.general.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BonusRule {
    STRIKE(2),
    SPARE(1);

    @Getter
    int throwsToUse;
}
