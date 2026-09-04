package com.svi.tictactoewebservice.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Pattern(regexp = "^[XO]$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Symbol {

    String message() default "Symbol must be X or O.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
