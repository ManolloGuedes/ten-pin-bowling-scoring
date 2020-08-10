package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;

public interface GameService {
    Game createGameUsing(String fileName);
    void calculateFinalResultOf(Game game);
    void calculateGameScore(Player player);
}
