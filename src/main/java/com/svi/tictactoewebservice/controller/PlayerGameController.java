package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.model.GameId;
import com.svi.tictactoewebservice.dto.response.GameListResponse;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.repository.file.FilePlayerRecordRepository;
import com.svi.tictactoewebservice.service.PlayerGameService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("/list-games")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerGameController {

    private final PlayerGameService playerGameService;

    public PlayerGameController() {
        PlayerRecordRepository playerRecordRepository =
                new FilePlayerRecordRepository();

        this.playerGameService =
                new PlayerGameService(playerRecordRepository);
    }

    @GET
    @Path("/{playerId}")
    public Response getGames(@PathParam("playerId") String playerId) {
        try {
            List<GameId> games = playerGameService.getGames(playerId);

            if (games == null) {
                return Response
                        .status(402)
                        .entity(new MessageResponse("Record not found"))
                        .build();
            }

            return Response
                    .ok(new GameListResponse(
                            games,
                            "Records found"
                    ))
                    .build();

        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse(
                            "The server ran into an unexpected exception."
                    ))
                    .build();
        }
    }
}