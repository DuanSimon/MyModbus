import code.RegisterRange;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import exception.ModbusTransportException;

public class Modbus {
    public static final int DEFAULT_MAX_READ_BIT_COUNT = 2000;
    public static final int DEFAULT_MAX_READ_REGISTER_COUNT = 125;
    public static final int DEFAULT_MAX_WRITE_REGISTER_COUNT = 120;

    private MessagingExceptionHandler exceptionHandler = new DefaultMessagingExceptionHandler();

    private int maxReadBitCount = DEFAULT_MAX_READ_BIT_COUNT;
    private int maxReadRegisterCount = DEFAULT_MAX_READ_REGISTER_COUNT;
    private int maxWriteRegisterCount = DEFAULT_MAX_WRITE_REGISTER_COUNT;

    public int getMaxReadCount(int registerRange){
        switch (registerRange){
            case RegisterRange.COIL_STATUS:
            case RegisterRange.INPUT_STATUS:
                return maxReadBitCount;
            case RegisterRange.HOLDING_REGISTER:
            case RegisterRange.INPUT_REGISTER:
                return maxReadRegisterCount;
        }
        return -1;
    }

    public void validateNumberOfRegisters(int registers) throws ModbusTransportException {
        if(registers < 1 || registers > maxReadRegisterCount){
            throw new ModbusTransportException("Invalid number of registers: " + registers);
        }
    }

    public void setExceptionHandler(MessagingExceptionHandler exceptionHandler){
        if(exceptionHandler == null){
            this.exceptionHandler = new DefaultMessagingExceptionHandler();
        }else{
            this.exceptionHandler = exceptionHandler;
        }
    }

    public MessagingExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public int getMaxReadBitCount() {
        return maxReadBitCount;
    }

    public void setMaxReadBitCount(int maxReadBitCount) {
        this.maxReadBitCount = maxReadBitCount;
    }

    public int getMaxReadRegisterCount() {
        return maxReadRegisterCount;
    }

    public void setMaxReadRegisterCount(int maxReadRegisterCount) {
        this.maxReadRegisterCount = maxReadRegisterCount;
    }

    public int getMaxWriteRegisterCount() {
        return maxWriteRegisterCount;
    }

    public void setMaxWriteRegisterCount(int maxWriteRegisterCount) {
        this.maxWriteRegisterCount = maxWriteRegisterCount;
    }
}
