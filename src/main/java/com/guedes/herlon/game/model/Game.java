package com.guedes.herlon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Game {
    private List<Player> players;

    public Boolean hasPlayer(String name) {
        return players.stream()
                    .anyMatch(player -> player.getName().equals(name));
    }
}
