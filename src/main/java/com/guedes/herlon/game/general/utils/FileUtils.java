package com.guedes.herlon.game.general.utils;

import com.guedes.herlon.game.exceptions.NoFileException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileUtils {
    public List<String> getLinesFromFile(String fileName) throws IOException {
        List<String> lines;
        try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
            lines = stream
                    .filter(line -> !line.isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (NoFileException e) {
            throw new NoFileException("Error on getLinesFromFile execution. The problem occurred trying to read " + fileName);
        }
        return lines;
    }

}
