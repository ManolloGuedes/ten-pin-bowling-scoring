package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Player {
    List<PlayerThrow> getThrows(List<Frame> frames);

    void calculateGameScore();

    String getName();

    List<Frame> getFrames();
}
