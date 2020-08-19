package com.guedes.herlon.game.model.interfaces;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ThrowDetails {
	/**
	 * Validates the bean content
	 * @return a Set of validation violations
	 */
	Set<ConstraintViolation<ThrowDetails>> validate();
	String getPlayerName();
	String getThrowResult();
}
