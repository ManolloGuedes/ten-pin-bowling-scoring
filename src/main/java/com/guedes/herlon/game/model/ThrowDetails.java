package com.guedes.herlon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class ThrowDetails {
    @NotEmpty(message = "Player name cannot be empty")
    private String playerName;

    @NotEmpty(message = "Throw result cannot be empty")
    @Pattern(regexp = "X|F|/|[0-9]", message = "Throw result should be a positive value between 0 and 9 or a strike (X), spare (/) and fault (F)")
    private String throwResult;

    public static ThrowDetails recoverThrowDetailsFrom(String line, String splitter) {
        String[] throwDetails = line.split(splitter);

        return new ThrowDetails(throwDetails[0], throwDetails[1].toUpperCase());
    }
}
