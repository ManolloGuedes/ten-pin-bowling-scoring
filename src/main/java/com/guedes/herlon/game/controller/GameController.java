package com.guedes.herlon.game.controller;

import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.service.interfaces.GameService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

@Controller
public class GameController implements CommandLineRunner {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void run(String... args) {
        if(args.length > 0) {
            log.info(String.format("Reading %s file", args[0]));
            Game game = gameService.createGameUsing(args[0]);
            gameService.calculateFinalResultOf(game);
            gameService.printFormattedScoreOf(game);
        } else {
            log.error("A file path was expected as input to the program's execution");
        }
    }

}
