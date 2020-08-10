package com.guedes.herlon.game.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static List<String> getLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
            lines = stream
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
