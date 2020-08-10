package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Frame {
    Long getTotalKnockedDownPins();
    boolean isStrike();
    boolean isSpare();
    List<PlayerThrow> getPlayerThrowList();
    Long getNumber();
    Long getScore();
    void setScore(Long score);
}
