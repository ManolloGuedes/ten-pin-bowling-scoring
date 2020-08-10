package com.guedes.herlon.game.controller;

import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

@Controller
public class GameController implements CommandLineRunner {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameController.class);

    @Override
    public void run(String... args) {
        if(args.length > 0) {
            log.info("Running...");
        } else {
            log.error("A file path was expected as input to the program's execution");
        }
    }

}
