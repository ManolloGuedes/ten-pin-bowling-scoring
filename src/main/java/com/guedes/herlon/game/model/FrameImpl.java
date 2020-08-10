package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.Frame;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @Override
    public boolean isStrike() {
        return playerThrowList.stream().anyMatch(PlayerThrow::getStrike);
    }

    @Override
    public boolean isSpare() {
        return playerThrowList.stream().anyMatch(PlayerThrow::getSpare);
    }
}
