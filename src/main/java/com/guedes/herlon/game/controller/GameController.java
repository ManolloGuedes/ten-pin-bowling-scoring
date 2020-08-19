package com.guedes.herlon.game.controller;

import com.guedes.herlon.game.exceptions.InvalidThrowException;
import com.guedes.herlon.game.exceptions.NoFileException;
import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.general.utils.FileUtils;
import com.guedes.herlon.game.model.ThrowDetails;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.service.interfaces.GameService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class GameController implements CommandLineRunner {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final FileUtils fileUtils;

    @Autowired
    public GameController(GameService gameService, FileUtils fileUtils) {
        this.gameService = gameService;
        this.fileUtils = fileUtils;
    }

    @Override
    public void run(String... args) {
        try {
            if (args.length > 0) {
                String file = args[0];
                log.info(String.format("Reading %s file", file));

                List<ThrowDetails> throwDetailsList = getThrowDetailsFrom(file);

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

    public List<ThrowDetails> getThrowDetailsFrom(String file) throws IOException {
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<ThrowDetails>> constraintViolations = new HashSet<>();

            List<ThrowDetails> throwDetailsList = new ArrayList<>();
            List<String> lines = fileUtils.getLinesFromFile(file);
            lines.forEach(line -> {
                ThrowDetails throwDetails = ThrowDetails.recoverThrowDetailsFrom(line, Constants.FILE_LINE_ELEMENT_SPLITTER);
                throwDetailsList.add(throwDetails);
                constraintViolations.addAll(validator.validate(throwDetails));
            });

            if(!constraintViolations.isEmpty()) {
                constraintViolations.forEach(throwDetailsConstraintViolation -> log.error(throwDetailsConstraintViolation.getMessage()));
                throw new InvalidThrowException("Error on getThrowDetailsFrom execution. The throw format is invalid");
            }

            return throwDetailsList;
        } catch (Exception e) {
            String errorMessage = "Error on getThrowDetailsFrom execution. Error while reading file line from file " + file;
            log.error(errorMessage, e);
            throw new NoFileException(errorMessage);
        }
    }

}
