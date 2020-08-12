package com.guedes.herlon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class ThrowDetails {
    @NotEmpty(message = "Player name cannot be empty")
    private String playerName;
    @NotEmpty(message = "Throw result cannot be empty")
    private String throwResult;

    public static ThrowDetails recoverThrowDetailsFrom(String line, String splitter) {
        String[] throwDetails = line.split(splitter);

        return new ThrowDetails(throwDetails[0], throwDetails[1]);
    }
}
