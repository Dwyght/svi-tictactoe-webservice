package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.annotation.RoomId;
import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.model.RecordId;
import com.svi.tictactoewebservice.service.RoomService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class RoomController {

    @Inject
    private RoomService roomService;

    // ========================================
    // GET ALL ROOMS
    // ========================================

    @GET
    @Path("/rooms")
    public Response getAllRooms() {
        List<RecordId> recordIds = roomService.getAllRooms();
        return Response.ok(new GameListResponse(recordIds, "Records found")).build();
    }

    // ========================================
    // GET ROOM GAMES
    // ========================================

    @GET
    @Path("/room/{roomId}/games")
    public Response getRoomGames(
            @PathParam("roomId")
            @RoomId
            String roomId) {
        List<RecordId> recordIds = roomService.getRoomGames(roomId);
        return Response.ok(new GameListResponse(recordIds, "Records found")).build();
    }
}
