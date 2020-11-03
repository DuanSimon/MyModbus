package com.simon.modbus4j.sero.messaging;

public class DefaultMessagingExceptionHandler implements MessagingExceptionHandler {
    public void receivedException(Exception e){
        e.printStackTrace();
    }
}
