package com.guedes.herlon.game.model.interfaces;

import java.util.List;

public interface Game {
    Boolean hasPlayer(String name);
    List<Player> getPlayers();
}
