package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.Game;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GameImpl implements Game {
    private List<Player> players;

    @Override
    public Boolean hasPlayer(String name) {
        return players.stream()
                    .anyMatch(player -> player.getName().equals(name));
    }
}
