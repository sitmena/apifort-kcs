package com.sitech.exception;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;

public class ExceptionHandlingServerCallListener<ReqT, RespT>
        extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
    private ServerCall<ReqT, RespT> serverCall;
    private Metadata metadata;

    ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall,
                                        Metadata metadata) {
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
        if (exception instanceof IllegalArgumentException) {
            serverCall.close(Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), metadata);
        } else {
            serverCall.close(Status.UNKNOWN, metadata);
        }
    }
}
