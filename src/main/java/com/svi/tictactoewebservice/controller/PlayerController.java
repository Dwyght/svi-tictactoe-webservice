package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.model.RecordId;
import com.svi.tictactoewebservice.service.GameService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/player")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PlayerController {

    @Inject
    private GameService gameService;

    @GET
    @Path("/{playerId}/games")
    public Response getPlayerGames(@PathParam("playerId") String playerId) {
        List<RecordId> recordIds = gameService.getPlayerGames(playerId);
        return Response.ok(new GameListResponse(recordIds, "Records found")).build();
    }
}
