package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.response.GameDetailsResponse;
import com.svi.tictactoewebservice.dto.response.GameIdResponse;
import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.GameSessionRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.repository.file.FileGameRecordRepository;
import com.svi.tictactoewebservice.repository.file.FilePlayerRecordRepository;
import com.svi.tictactoewebservice.repository.memory.InMemoryGameSessionRepository;
import com.svi.tictactoewebservice.service.GameService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GameController {

    private final GameService gameService;

    public GameController() {

        GameRecordRepository gameRecordRepository =
                new FileGameRecordRepository();

        PlayerRecordRepository playerRecordRepository =
                new FilePlayerRecordRepository();

        GameSessionRepository gameSessionRepository =
                new InMemoryGameSessionRepository();

        this.gameService = new GameService(
                gameRecordRepository,
                playerRecordRepository,
                gameSessionRepository
        );
    }

    // ========================================
    // SAVE MOVE
    // ========================================

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(SaveRequest request) {

        gameService.save(request);

        return Response
                .ok(new MessageResponse("Record saved."))
                .build();
    }

    // ========================================
    // GET PLAYER GAMES
    // ========================================

    @GET
    @Path("/list-games/{playerId}")
    public Response getPlayerGames(
            @PathParam("playerId") String playerId) {

        List<GameId> games =
                gameService.getPlayerGames(playerId);

        return Response
                .ok(new GameListResponse(
                        games,
                        "Records found"
                ))
                .build();
    }

    // ========================================
    // GET GAME DETAILS
    // ========================================

    @GET
    @Path("/game/{gameId}")
    public Response getGame(
            @PathParam("gameId") String gameId) {

        List<MoveRecord> records =
                gameService.getGame(gameId);

        return Response
                .ok(new GameDetailsResponse(
                        records,
                        "Records found"
                ))
                .build();
    }

    // ========================================
    // CREATE NEW GAME ID
    // ========================================

    @POST
    @Path("/session/{gameCode}/game")
    public Response createGameId(
            @PathParam("gameCode") String gameCode) {

        String gameId =
                gameService.createGameId(gameCode);

        return Response
                .ok(new GameIdResponse(gameId))
                .build();
    }

    // ========================================
    // GET CURRENT GAME ID
    // ========================================

    @GET
    @Path("/session/{gameCode}/game")
    public Response getCurrentGameId(
            @PathParam("gameCode") String gameCode) {

        String gameId =
                gameService.getCurrentGameId(gameCode);

        return Response
                .ok(new GameIdResponse(gameId))
                .build();
    }
}