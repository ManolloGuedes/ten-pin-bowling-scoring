package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.PlayerThrow;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Implementation of the Frame interface.
 * @author herlon-guedes
 * @since 08/10/2020
 */
@Data
@RequiredArgsConstructor
public class FrameImpl implements Frame {

    @NonNull
    private List<PlayerThrow> playerThrowList;
    @NonNull
    private Long number;
    private Long score;

    @Override
    public Long getTotalKnockedDownPins() {
        return playerThrowList.stream()
                .mapToLong(PlayerThrow::getKnockedDownPins)
                .sum();
    }

    /**
     * Checks for a strike in the PlayerThrow list
     * @return boolean
     */
    @Override
    public boolean isStrike() {
        return playerThrowList.stream().anyMatch(PlayerThrow::getStrike);
    }

    /**
     * Checks for a spare in the PlayerThrow list
     * @return boolean
     */
    @Override
    public boolean isSpare() {
        return playerThrowList.stream().anyMatch(PlayerThrow::getSpare);
    }

}
