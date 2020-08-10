package com.guedes.herlon.game.controller;

import com.guedes.herlon.game.service.GameService;
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
            gameService.readInputFile(args[0]);
        } else {
            log.error("A file path was expected as input to the program's execution");
        }
    }

}
