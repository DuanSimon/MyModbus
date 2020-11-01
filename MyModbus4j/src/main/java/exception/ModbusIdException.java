package exception;

public class ModbusIdException extends RuntimeException {
    private static final long serialVersionUID = -1;

    public ModbusIdException(String message){
        super(message);
    }

    public ModbusIdException(Throwable cause){
        super(cause);
    }
}
