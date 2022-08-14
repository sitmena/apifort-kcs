package com.sitech.exception;

import javax.ws.rs.WebApplicationException;
import java.io.Serializable;

public class ApiFortException extends WebApplicationException implements Serializable {

    private static final long serialVersionUID = 1L;

    public ApiFortException() {
        super();
    }

    public ApiFortException(String msg) {
        super(msg);
    }

    public ApiFortException(String msg, Exception e) {
        super(msg, e);
    }
}
