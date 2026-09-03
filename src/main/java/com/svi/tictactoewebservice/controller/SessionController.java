package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.request.EmoteRequest;
import com.svi.tictactoewebservice.dto.request.PlayerSessionRequest;
import com.svi.tictactoewebservice.dto.request.ScoreRequest;
import com.svi.tictactoewebservice.dto.response.GameIdResponse;
import com.svi.tictactoewebservice.dto.response.GameSessionResponse;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.model.GameSession;
import com.svi.tictactoewebservice.service.GameService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class SessionController {

    @Inject
    private GameService gameService;

    // ========================================
    // CREATE NEW GAME ID
    // ========================================

    @POST
    @Path("/session/{gameCode}/game")
    public Response createGameId(@PathParam("gameCode") String gameCode) {
        String gameId = gameService.createGameId(gameCode);
        return Response.ok(new GameIdResponse(gameId)).build();
    }

    // ========================================
    // GET CURRENT GAME ID
    // ========================================

    @GET
    @Path("/session/{gameCode}/game")
    public Response getCurrentGameId(@PathParam("gameCode") String gameCode) {
        String gameId = gameService.getCurrentGameId(gameCode);
        return Response.ok(new GameIdResponse(gameId)).build();
    }

    // ========================================
    // REGISTER PLAYER
    // ========================================

    @POST
    @Path("/session/{gameCode}/player")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerPlayer(@PathParam("gameCode") String gameCode,
            @Valid PlayerSessionRequest request) {
        gameService.registerPlayer(gameCode, request);
        return Response.ok(new MessageResponse("Player registered.")).build();
    }

    // ========================================
    // UPDATE SCORE
    // ========================================

    @POST
    @Path("/session/{gameCode}/score")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateScore(@PathParam("gameCode") String gameCode,
            @Valid ScoreRequest request) {
        gameService.updateScore(gameCode, request);
        return Response.ok(new MessageResponse("Score updated.")).build();
    }

    // ========================================
    // SEND EMOTE
    // ========================================

    @POST
    @Path("/session/{gameCode}/emote")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmote(@PathParam("gameCode") String gameCode,
            @Valid EmoteRequest request) {
        gameService.sendEmote(gameCode, request);
        return Response.ok(new MessageResponse("Emote sent.")).build();
    }

    // ========================================
    // GET FULL RUNTIME SESSION
    // ========================================

    @GET
    @Path("/session/{gameCode}")
    public Response getSession(@PathParam("gameCode") String gameCode) {
        GameSession session = gameService.getSession(gameCode);
        return Response.ok(new GameSessionResponse(session)).build();
    }
}
