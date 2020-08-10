package com.guedes.herlon.game.model.interfaces;

import com.guedes.herlon.game.model.PlayerThrow;

public interface Frame {
    Long getTotalKnockedDownPins();

    boolean isStrike();

    boolean isSpare();

    java.util.List<PlayerThrow> getPlayerThrowList();

    Long getNumber();

    Long getScore();

    void setPlayerThrowList(java.util.List<PlayerThrow> playerThrowList);

    void setNumber(Long number);

    void setScore(Long score);
}
