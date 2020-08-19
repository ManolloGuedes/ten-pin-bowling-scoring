package com.guedes.herlon.game.general.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Definition of bonus calculation rules.
 * Each BonusRule enum has a number of throws to use on a bonus calculation.
 * @since 08/10/2020
 * @author herlon-guedes
 */
@AllArgsConstructor
public enum BonusRule {
    STRIKE(2),
    SPARE(1);

    @Getter
    int throwsToUse;
}
