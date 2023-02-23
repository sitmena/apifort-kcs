package com.sitech.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.quarkus.security.UnauthorizedException;


import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

public class ExceptionHandlingServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
    private ServerCall<ReqT, RespT> serverCall;
    private Metadata metadata;

    ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        super(listener);
        this.serverCall = serverCall;
        this.metadata = metadata;
    }

    @Override
    public void onHalfClose() {
        try {
            super.onHalfClose();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
            throw ex;
        }
    }

    @Override
    public void onReady() {
        try {
            super.onReady();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
            throw ex;
        }
    }

    private void handleException(RuntimeException exception, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        try {
            if (exception instanceof UnauthorizedException) {
                serverCall.close(Status.UNAUTHENTICATED.withDescription(new ObjectMapper().writeValueAsString(exception.getMessage())), metadata);
            } else if (exception instanceof ResourceNotFoundException) {
                serverCall.close(Status.NOT_FOUND.withDescription(new ObjectMapper().writeValueAsString(exception.getMessage())), metadata);
            } else if (exception instanceof DataConflictException) {
                serverCall.close(Status.ALREADY_EXISTS.withDescription(exception.getMessage()), metadata);
            } else if (exception instanceof InternalServerErrorException) {
                serverCall.close(Status.INTERNAL.withDescription(new ObjectMapper().writeValueAsString(exception.getMessage())), metadata);
            } else if (exception instanceof NotFoundException) {
                serverCall.close(Status.NOT_FOUND.withDescription(new ObjectMapper().writeValueAsString(exception.getMessage())), metadata);
            }else {
                serverCall.close(Status.CANCELLED, metadata);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
