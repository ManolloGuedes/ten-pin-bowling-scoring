package com.guedes.herlon.game.model;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThrowDetails {
    @NotEmpty(message = "Player name cannot be empty")
    private String playerName;

    @NotEmpty(message = "Throw result cannot be empty")
    @Pattern(regexp = "X|F|/|[0-9]|10", message = "Throw result should be a positive value between 0 and 10 or a strike (X), spare (/) and fault (F)")
    private String throwResult;

    public Set<ConstraintViolation<ThrowDetails>>  validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return validator.validate(this);
    }
}
