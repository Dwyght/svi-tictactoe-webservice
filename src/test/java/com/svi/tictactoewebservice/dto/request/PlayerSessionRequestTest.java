package com.svi.tictactoewebservice.dto.request;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.mapper.ConstraintViolationExceptionMapper;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PlayerSessionRequest")
class PlayerSessionRequestTest {

    private static final List<String> VALID_SUSHI_IDS = Arrays.asList(
            "x-sushi-1",
            "x-sushi-2",
            "x-sushi-3",
            "x-sushi-4",
            "x-sushi-5",
            "o-sushi-1",
            "o-sushi-2",
            "o-sushi-3",
            "o-sushi-4",
            "o-sushi-5");

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation
                .byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("invalid sushi ID is mapped to a 400 response")
    void invalidSushiIdIsMappedToBadRequest() {
        PlayerSessionRequest request = validRequest();
        request.setSushiid("banana");
        Set<ConstraintViolation<PlayerSessionRequest>> violations = validator.validate(request);

        Response response = new ConstraintViolationExceptionMapper().toResponse(
                new ConstraintViolationException(violations));

        MessageResponse body = (MessageResponse) response.getEntity();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Invalid sushi ID.", body.getMsg());
    }

    @Test
    @DisplayName("every sushi ID offered by the frontend passes validation")
    void frontendSushiIdsPassValidation() {
        for (String sushiId : VALID_SUSHI_IDS) {
            PlayerSessionRequest request = validRequest();
            request.setSymbol(sushiId.startsWith("x-") ? "X" : "O");
            request.setSushiid(sushiId);

            assertTrue(validator.validate(request).isEmpty(), sushiId);
        }
    }

    private PlayerSessionRequest validRequest() {
        PlayerSessionRequest request = new PlayerSessionRequest();
        request.setPlayerid("player_1");
        request.setSymbol("X");
        request.setSushiid("x-sushi-1");
        return request;
    }
}
