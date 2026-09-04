package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("SessionController")
class SessionControllerTest extends JerseyValidationTestSupport {

    private static final String INVALID_GAME_CODE = "bad!";

    private GameService gameService;

    @Override
    protected Application configure() {
        gameService = mock(GameService.class);
        return validationApplication(SessionController.class, gameService, GameService.class);
    }

    @Test
    @DisplayName("createGameId rejects unsafe game codes with 400")
    void createGameIdRejectsInvalidPathParameter() {
        assertPostRejected(
                "session/" + INVALID_GAME_CODE + "/game",
                "",
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("getCurrentGameId rejects unsafe game codes with 400")
    void getCurrentGameIdRejectsInvalidPathParameter() {
        assertGetRejected(
                "session/" + INVALID_GAME_CODE + "/game",
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("registerPlayer rejects unsafe game codes with 400")
    void registerPlayerRejectsInvalidPathParameter() {
        assertPostRejected(
                "session/" + INVALID_GAME_CODE + "/player",
                "{\"playerid\":\"alice\",\"symbol\":\"X\","
                        + "\"sushiid\":\"x-sushi-1\"}",
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("updateScore rejects unsafe game codes with 400")
    void updateScoreRejectsInvalidPathParameter() {
        assertPostRejected(
                "session/" + INVALID_GAME_CODE + "/score",
                "{\"xscore\":0,\"oscore\":0}",
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("sendEmote rejects unsafe game codes with 400")
    void sendEmoteRejectsInvalidPathParameter() {
        assertPostRejected(
                "session/" + INVALID_GAME_CODE + "/emote",
                "{\"symbol\":\"X\",\"emoteid\":\"happy\"}",
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("getSession rejects unsafe game codes with 400")
    void getSessionRejectsInvalidPathParameter() {
        assertGetRejected(
                "session/" + INVALID_GAME_CODE,
                "Invalid game code.");

        verifyNoInteractions(gameService);
    }
}
