package com.simon.modbus4j.sero;

public class ShouldNeverHappenException extends RuntimeException {
    private static final long serialVersionUID = -1;

    public ShouldNeverHappenException(String message){
        super(message);
    }

    public ShouldNeverHappenException(Throwable cause){
        super(cause);
    }
}
