package com.guedes.herlon.game.controller;

import com.guedes.herlon.game.exceptions.NoFileException;
import com.guedes.herlon.game.general.utils.ThrowUtils;
import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.service.interfaces.GameService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController implements CommandLineRunner {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final ThrowUtils throwUtils;

    @Autowired
    public GameController(GameService gameService, ThrowUtils throwUtils) {
        this.gameService = gameService;
        this.throwUtils = throwUtils;
    }

    @Override
    public void run(String... args) {
        try {
            if (args.length > 0) {
                String file = args[0];
                log.info(String.format("Reading %s file", file));

                List<ThrowDetails> throwDetailsList = throwUtils.getThrowDetailsFrom(file);

                Game game = gameService.createGameUsing(throwDetailsList);
                gameService.calculateFinalResultOf(game);
                System.out.println();
                System.out.println(game.toString());
            } else {
                throw new NoFileException("A file path was expected as input to the program's execution");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
