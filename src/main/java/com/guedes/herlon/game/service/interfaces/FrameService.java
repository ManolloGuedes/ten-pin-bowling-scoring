package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;

import java.util.List;

public interface FrameService {
    long calculateFrameScore(List<Frame> frames, int frameNumber);
    List<PlayerThrow> getThrowsFrom(List<Frame> frames);
}
