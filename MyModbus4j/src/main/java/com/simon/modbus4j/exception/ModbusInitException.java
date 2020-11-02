package com.simon.modbus4j.exception;

public class ModbusInitException extends Exception {
    private static final long serialVersionUID = -1;

    public ModbusInitException() {
        super();
    }

    public ModbusInitException(String name, Throwable cause) {
        super(message, cause);
    }

    public ModbusInitException(String message) {
        super(message);
    }

    public ModbusInitException(Throwable cause) {
        super(cause);
    }

}
