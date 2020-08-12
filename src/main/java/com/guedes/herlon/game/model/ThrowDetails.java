package com.guedes.herlon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThrowDetails {
    private String playerName;
    private String throwResult;

    public static ThrowDetails recoverThrowDetailsFrom(String line, String splitter) {
        String[] throwDetails = line.split(splitter);

        return new ThrowDetails(throwDetails[0], throwDetails[1]);
    }
}
