package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RecordNotFoundExceptionMapper
        implements ExceptionMapper<RecordNotFoundException> {

    @Override
    public Response toResponse(RecordNotFoundException exception) {
        return Response
                .status(402)
                .type(MediaType.APPLICATION_JSON)
                .entity(new MessageResponse(exception.getMessage()))
                .build();
    }
}