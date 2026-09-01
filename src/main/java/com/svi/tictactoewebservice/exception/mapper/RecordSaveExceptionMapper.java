package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.RecordSaveException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RecordSaveExceptionMapper
        implements ExceptionMapper<RecordSaveException> {

    @Override
    public Response toResponse(RecordSaveException exception) {
        return Response
                .status(401)
                .type(MediaType.APPLICATION_JSON)
                .entity(new MessageResponse("Record could not be saved"))
                .build();
    }
}