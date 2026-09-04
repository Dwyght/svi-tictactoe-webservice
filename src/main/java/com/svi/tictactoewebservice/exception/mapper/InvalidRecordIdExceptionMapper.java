package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.InvalidRecordIdException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidRecordIdExceptionMapper
        implements ExceptionMapper<InvalidRecordIdException> {

    @Override
    public Response toResponse(InvalidRecordIdException exception) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new MessageResponse(exception.getMessage()))
                .build();
    }
}
