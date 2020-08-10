package com.guedes.herlon.game.model.interfaces;

import com.guedes.herlon.game.model.Player;

public interface Game {
    Boolean hasPlayer(String name);

    java.util.List<Player> getPlayers();

    void setPlayers(java.util.List<Player> players);
}
