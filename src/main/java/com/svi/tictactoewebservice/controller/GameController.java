package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.response.GameDetailsResponse;
import com.svi.tictactoewebservice.model.MoveRecord;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.file.FileGameRecordRepository;
import com.svi.tictactoewebservice.service.GameService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
public class GameController {

    private final GameService gameService;

    public GameController() {
        GameRecordRepository gameRecordRepository =
                new FileGameRecordRepository();

        this.gameService =
                new GameService(gameRecordRepository);
    }

    @GET
    @Path("/{gameId}")
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
}