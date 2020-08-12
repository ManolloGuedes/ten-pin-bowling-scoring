package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;

import java.io.IOException;

public interface GameService {
    Game createGameUsing(String fileName) throws IOException;
    void calculateFinalResultOf(Game game);
    void calculateGameScore(Player player);
}
