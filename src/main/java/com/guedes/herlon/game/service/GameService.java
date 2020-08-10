package com.guedes.herlon.game.service;

import com.guedes.herlon.game.model.Game;

public interface GameService {
    Game createGameUsing(String fileName);
    void calculateFinalResultOf(Game game);
}
