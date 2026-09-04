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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SaveRequest")
class SaveRequestTest {

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
    @DisplayName("out-of-range and non-numeric locations are mapped to 400 responses")
    void invalidLocationsAreMappedToBadRequest() {
        assertLocationMapsToBadRequest("9");
        assertLocationMapsToBadRequest("banana");
    }

    @Test
    @DisplayName("all frontend board locations from 0 through 8 pass validation")
    void frontendBoardLocationsPassValidation() {
        for (int location = 0; location <= 8; location++) {
            SaveRequest request = validRequest();
            request.setLocation(String.valueOf(location));

            assertTrue(validator.validate(request).isEmpty(), String.valueOf(location));
        }
    }

    private void assertLocationMapsToBadRequest(String location) {
        SaveRequest request = validRequest();
        request.setLocation(location);
        Set<ConstraintViolation<SaveRequest>> violations = validator.validate(request);

        Response response = new ConstraintViolationExceptionMapper().toResponse(
                new ConstraintViolationException(violations));

        MessageResponse body = (MessageResponse) response.getEntity();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Location must be a valid board position.", body.getMsg());
    }

    private SaveRequest validRequest() {
        SaveRequest request = new SaveRequest();
        request.setRoomid("AB2CD3EF");
        request.setGameid("9e2f06bf-1c2d-4e2c-9c92-55d8fb1dc934");
        request.setPlayerid("player_1");
        request.setSymbol("X");
        request.setLocation("0");
        request.setDatesave("2026-09-04T09:00:00Z");
        return request;
    }
}
