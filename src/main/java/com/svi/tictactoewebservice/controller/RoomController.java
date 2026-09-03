package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.model.GameId;
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
        List<GameId> rooms = roomService.getAllRooms();
        return Response.ok(new GameListResponse(rooms, "Records found")).build();
    }

    // ========================================
    // GET ROOM GAMES
    // ========================================

    @GET
    @Path("/room/{roomId}/games")
    public Response getRoomGames(@PathParam("roomId") String roomId) {
        List<GameId> games = roomService.getRoomGames(roomId);
        return Response.ok(new GameListResponse(games, "Records found")).build();
    }
}
