package com.sitech.exception;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String exception) {
        super(exception);
    }
}
