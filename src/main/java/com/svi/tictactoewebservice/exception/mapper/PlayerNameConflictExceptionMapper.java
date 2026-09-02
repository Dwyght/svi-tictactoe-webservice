package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PlayerNameConflictExceptionMapper
        implements ExceptionMapper<PlayerNameConflictException> {

    @Override
    public Response toResponse(PlayerNameConflictException exception) {
        return Response
                .status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new MessageResponse(exception.getMessage()))
                .build();
    }
}
