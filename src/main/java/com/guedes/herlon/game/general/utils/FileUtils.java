package com.guedes.herlon.game.general.utils;

import com.guedes.herlon.game.exceptions.NoFileException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utils class to manipulate files producing inputs for the software.
 * @author herlon-guedes
 * @since 08/10/2020
 */
@Service
public class FileUtils {
    /**
     * Reads a file and splits it into a list of lines.
     * @param filePath absolute path to the file that needs to be read
     * @return list of file lines
     * @throws IOException when occurs a problem while opening the file
     */
    public List<String> getLinesFromFile(String filePath) throws IOException {
        List<String> lines;
        try(Stream<String> stream = Files.lines(Paths.get(filePath))) {
            lines = stream
                    .filter(line -> !line.isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (NoFileException e) {
            throw new NoFileException("Error on getLinesFromFile execution. The problem occurred trying to read " + filePath);
        }
        return lines;
    }

}
