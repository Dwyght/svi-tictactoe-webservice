package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.mapper.ConstraintViolationExceptionMapper;
import com.svi.tictactoewebservice.service.GameService;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameController")
class GameControllerTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController controller;

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
    @DisplayName("save rejects a missing required field with 400 before invoking the service")
    void saveRejectsMissingFieldBeforeInvokingService() throws NoSuchMethodException {
        SaveRequest request = validSaveRequest();
        request.setRoomid(null);
        Method saveMethod = GameController.class.getMethod("save", SaveRequest.class);

        Set<ConstraintViolation<GameController>> violations = validator
                .forExecutables()
                .validateParameters(controller, saveMethod, new Object[]{request});

        assertFalse(violations.isEmpty());
        Response response = new ConstraintViolationExceptionMapper().toResponse(
                new ConstraintViolationException(violations));
        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus()),
                () -> assertEquals("Room ID is required.", body.getMsg()));
        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("SaveRequest declares all required field and format constraints")
    void saveRequestDeclaresRequiredConstraints() {
        SaveRequest blankRoomId = validSaveRequest();
        blankRoomId.setRoomid("   ");
        SaveRequest blankGameId = validSaveRequest();
        blankGameId.setGameid("   ");
        SaveRequest blankPlayerId = validSaveRequest();
        blankPlayerId.setPlayerid("   ");
        SaveRequest invalidPlayerId = validSaveRequest();
        invalidPlayerId.setPlayerid("invalid!");
        SaveRequest blankSymbol = validSaveRequest();
        blankSymbol.setSymbol("   ");
        SaveRequest invalidSymbol = validSaveRequest();
        invalidSymbol.setSymbol("A");
        SaveRequest blankLocation = validSaveRequest();
        blankLocation.setLocation("   ");
        SaveRequest blankDateSave = validSaveRequest();
        blankDateSave.setDatesave("   ");

        assertAll(
                () -> assertViolation(blankRoomId, "roomid", "Room ID is required."),
                () -> assertViolation(blankGameId, "gameid", "Game ID is required."),
                () -> assertViolation(blankPlayerId, "playerid", "Player ID is required."),
                () -> assertViolation(invalidPlayerId, "playerid", "Invalid player ID."),
                () -> assertViolation(blankSymbol, "symbol", "Symbol is required."),
                () -> assertViolation(invalidSymbol, "symbol", "Symbol must be X or O."),
                () -> assertViolation(blankLocation, "location", "Location is required."),
                () -> assertViolation(blankDateSave, "datesave", "Date saved is required."));
    }

    private void assertViolation(
            SaveRequest request,
            String property,
            String expectedMessage) {
        Set<ConstraintViolation<SaveRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(violation ->
                property.equals(violation.getPropertyPath().toString())
                        && expectedMessage.equals(violation.getMessage())));
    }

    private SaveRequest validSaveRequest() {
        SaveRequest request = new SaveRequest();
        request.setRoomid("room-1");
        request.setGameid("game-1");
        request.setPlayerid("player_1");
        request.setSymbol("X");
        request.setLocation("top-left");
        request.setDatesave("2026-09-04T09:00:00Z");
        return request;
    }
}
