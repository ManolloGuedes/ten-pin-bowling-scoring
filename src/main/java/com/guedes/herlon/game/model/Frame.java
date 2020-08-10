package com.guedes.herlon.game.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Frame {

    @NonNull
    private List<PlayerThrow> playerThrowList;
    @NonNull
    private Long number;
    private Long score;

    public Long getTotalKnockedDownPins() {
        return playerThrowList.stream()
                .mapToLong(PlayerThrow::getKnockedDownPins)
                .sum();
    }

}
