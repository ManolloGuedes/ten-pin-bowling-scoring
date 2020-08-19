package com.guedes.herlon.game.model;

import com.guedes.herlon.game.general.Constants;
import com.guedes.herlon.game.model.interfaces.Game;
import com.guedes.herlon.game.model.interfaces.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of the Game interface.
 * @author herlon-guedes
 * @since 08/10/2020
 */
@Data
@AllArgsConstructor
public class GameImpl implements Game {
    private List<Player> players;

    @Override
    public Boolean hasPlayer(String name) {
        return players.stream()
                    .anyMatch(player -> player.getName().equals(name));
    }

    /**
     * Returns a string formatted as the following example:
     * Frame        1       2       3       4       5       6       7       8       9       10
     * Player1
     * Pinfalls         X   7   /   9   0       X   0   8   8   /   F   6       X       X   X   8   1
     * Score        20      39      48      66      74      84      90      120     148     167
     * @return
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Frame");
        IntStream.rangeClosed(1, 10)
                .forEach(frameNumber -> stringBuilder.append("\t\t".concat(String.valueOf(frameNumber))));
        stringBuilder.append("\n");
        this.getPlayers().forEach(player -> {
            stringBuilder
                    .append(player.getName())
                    .append("\n")
                    .append("Pinfalls\t")
                    .append(player.getFrames()
                            .stream()
                            .map(frame -> frame.getPlayerThrowList().stream().map(playerThrow -> {
                                if(playerThrow.getStrike()) {
                                    String suffix = "";
                                    if (frame.getPlayerThrowList().size() != Constants.LAST_FRAME_MAX_NUMBER_THROWS) {
                                        suffix = "\t";
                                    }
                                    return suffix.concat(Constants.STRIKE_CHARACTER);
                                } else if (playerThrow.getSpare()) {
                                    return Constants.SPARE_CHARACTER;
                                } else if (playerThrow.getFault()) {
                                    return Constants.FAULT_CHARACTER;
                                }
                                return playerThrow.getKnockedDownPins().toString();
                            }).collect(Collectors.joining("\t"))).collect(Collectors.joining("\t"))
                    ).append("\n")
                    .append("Score\t\t")
                    .append(player.getFrames()
                            .stream()
                            .map(frame -> frame.getScore().toString())
                            .collect(Collectors.joining("\t\t"))
                    ).append("\n");
        });

        return stringBuilder.toString();
    }
}
