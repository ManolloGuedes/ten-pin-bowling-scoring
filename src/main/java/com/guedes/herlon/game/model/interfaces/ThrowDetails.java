package com.guedes.herlon.game.model.interfaces;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ThrowDetails {
	Set<ConstraintViolation<ThrowDetails>> validate();
	String getPlayerName();
	String getThrowResult();
}
