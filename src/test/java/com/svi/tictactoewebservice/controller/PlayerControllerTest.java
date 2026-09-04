package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("PlayerController")
class PlayerControllerTest extends JerseyValidationTestSupport {

    private GameService gameService;

    @Override
    protected Application configure() {
        gameService = mock(GameService.class);
        return validationApplication(PlayerController.class, gameService, GameService.class);
    }

    @Test
    @DisplayName("getPlayerGames rejects unsafe player IDs with 400")
    void getPlayerGamesRejectsInvalidPathParameter() {
        assertGetRejected("player/bad!/games", "Invalid player ID.");

        verifyNoInteractions(gameService);
    }
}
