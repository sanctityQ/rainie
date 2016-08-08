package com.itiancai.galaxy.dts;

public class DTSException extends RuntimeException {

    public DTSException(){}

    public DTSException(String message){
        super(message);
    }

    public DTSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DTSException(Throwable cause) {
        super(cause);
    }

}
