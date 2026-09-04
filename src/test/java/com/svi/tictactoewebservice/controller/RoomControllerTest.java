package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("RoomController")
class RoomControllerTest extends JerseyValidationTestSupport {

    private RoomService roomService;

    @Override
    protected Application configure() {
        roomService = mock(RoomService.class);
        return validationApplication(RoomController.class, roomService, RoomService.class);
    }

    @Test
    @DisplayName("getRoomGames rejects unsafe room IDs with 400")
    void getRoomGamesRejectsInvalidPathParameter() {
        assertGetRejected("room/bad!/games", "Invalid room ID.");

        verifyNoInteractions(roomService);
    }
}
