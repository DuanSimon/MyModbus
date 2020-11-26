package com.simon.modbus4j.sero;

public class NotImplementedException extends RuntimeException {
    static final long serialVersionUID = -1;

    public NotImplementedException(){
        super();
    }

    public NotImplementedException(String message){
        super(message);
    }
}
