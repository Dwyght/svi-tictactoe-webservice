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
@Pattern(regexp = "^[A-Za-z0-9_-]{1,10}$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayerId {

    String message() default "Invalid player ID.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
