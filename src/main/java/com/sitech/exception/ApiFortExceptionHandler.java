package com.sitech.exception;

import io.quarkus.arc.Priority;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(9999)
public class ApiFortExceptionHandler implements ExceptionMapper<ApiFortException> {
    @Override
    public Response toResponse(ApiFortException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage(exception.getMessage() , false)).build();
    }
}
