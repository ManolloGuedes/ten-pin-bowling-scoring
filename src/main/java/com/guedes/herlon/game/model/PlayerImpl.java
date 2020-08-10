package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.Frame;
import com.guedes.herlon.game.model.interfaces.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlayerImpl implements Player {

    private String name;
    private List<Frame> frames;
}
