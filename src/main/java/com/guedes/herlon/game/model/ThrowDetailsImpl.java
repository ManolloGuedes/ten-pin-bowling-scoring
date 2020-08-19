package com.guedes.herlon.game.model;

import com.guedes.herlon.game.model.interfaces.ThrowDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * Implementation of the ThrowDetails interface.
 * @author herlon-guedes
 * @since 08/11/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThrowDetailsImpl implements ThrowDetails {
    @NotEmpty(message = "Player name cannot be empty")
    private String playerName;

    @NotEmpty(message = "Throw result cannot be empty")
    @Pattern(regexp = "X|F|/|[0-9]|10", message = "Throw result should be a positive value between 0 and 10 or a strike (X), spare (/) and fault (F)")
    private String throwResult;

    /**
     * Uses the attributes constraint validation notations to validate the ThrowDetails
     * @return a Set of validation violations
     */
    @Override
    public Set<ConstraintViolation<ThrowDetails>>  validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return validator.validate(this);
    }
}
