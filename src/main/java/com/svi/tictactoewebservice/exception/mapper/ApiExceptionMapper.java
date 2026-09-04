package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.ApiException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    private static final Logger LOGGER =
            Logger.getLogger(ApiExceptionMapper.class.getName());

    @Override
    public Response toResponse(ApiException exception) {
        LOGGER.log(
                Level.INFO,
                "Handled API exception " + exception.getClass().getSimpleName()
                        + ": " + exception.getMessage());

        return Response
                .status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new MessageResponse(exception.getMessage()))
                .build();
    }
}
