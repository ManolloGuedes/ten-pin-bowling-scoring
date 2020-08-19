package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;

import java.util.List;

public interface FrameService {
    /**
     * Calculates the score for a specific frame defined by frameNumber
     * @param frames list of frame instances
     * @param frameNumber frame number that should have its score calculated
     * @return score of the specific frame
     */
    long calculateFrameScore(List<Frame> frames, int frameNumber);

    /**
     * Gets a list of all frame throws
     * @param frames list of Frame instance
     * @return List of PlayerThrow instances
     */
    List<PlayerThrow> getThrowsFrom(List<Frame> frames);
}
