package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.request.SaveRequest;
import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.repository.GameRecordRepository;
import com.svi.tictactoewebservice.repository.PlayerRecordRepository;
import com.svi.tictactoewebservice.repository.file.FileGameRecordRepository;
import com.svi.tictactoewebservice.repository.file.FilePlayerRecordRepository;
import com.svi.tictactoewebservice.service.SaveService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/save")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SaveController {

    private final SaveService saveService;

    public SaveController() {
        GameRecordRepository gameRecordRepository =
                new FileGameRecordRepository();

        PlayerRecordRepository playerRecordRepository =
                new FilePlayerRecordRepository();

        this.saveService = new SaveService(
                gameRecordRepository,
                playerRecordRepository
        );
    }

    @POST
    public Response save(SaveRequest request) {
        try {
            saveService.save(request);

            return Response
                    .ok(new MessageResponse("Record saved."))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response
                    .status(401)
                    .entity(new MessageResponse("Record could not be saved"))
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