import code.ExceptionCode;

public class ExceptionResult {
    private final byte exceptionCode;
    private final String exceptionMessage;

    public ExceptionResult(byte exceptionCode){
        this.exceptionCode = exceptionCode;
        exceptionMessage = ExceptionCode.getExceptionMessage(exceptionCode);
    }

    public byte getExceptionCode(){
        return exceptionCode;
    }

    public String getExceptionMessage(){
        return exceptionMessage;
    }
}
