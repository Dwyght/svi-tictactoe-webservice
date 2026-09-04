package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.response.GameDetailsResponse;
import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.model.Game;
import com.svi.tictactoewebservice.model.RecordId;
import com.svi.tictactoewebservice.service.GameService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class GameController {

    @Inject
    private GameService gameService;

    // ========================================
    // SAVE MOVE
    // ========================================

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(SaveRequest request) {
        gameService.save(request);
        return Response.ok(new MessageResponse("Record saved.")).build();
    }

    // ========================================
    // GET ALL GAMES
    // ========================================

    @GET
    @Path("")
    public Response getAllGames() {
        List<RecordId> recordIds = gameService.getAllGames();
        return Response.ok(new GameListResponse(recordIds, "Records found")).build();
    }

    // ========================================
    // GET GAME DETAILS
    // ========================================

    @GET
    @Path("/{gameId}")
    public Response getGame(@PathParam("gameId") String gameId) {
        List<Game> records = gameService.getGame(gameId);
        return Response.ok(new GameDetailsResponse(records, "Records found")).build();
    }

}
