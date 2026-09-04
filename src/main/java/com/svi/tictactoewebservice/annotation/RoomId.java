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
@Pattern(regexp = "^[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{8}$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoomId {

    String message() default "Invalid room ID.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
