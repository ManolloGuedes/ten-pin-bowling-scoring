package com.guedes.herlon.game.service.interfaces;

import com.guedes.herlon.game.model.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;

import java.io.IOException;
import java.util.List;

public interface GameService {
    Game createGameUsing(List<ThrowDetails> throwDetailsList) throws IOException;
    void calculateFinalResultOf(Game game);
    void calculateGameScore(Player player);
}
