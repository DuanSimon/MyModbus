package com.simon.modbus4j;

import com.simon.modbus4j.code.ExceptionCode;

public class ExceptionResult {
    private final byte exceptionCode;
    private final String exceptionMessage;

    public ExceptionResult(byte exceptionCode) {
        this.exceptionCode = exceptionCode;
        exceptionMessage = ExceptionCode.getExceptionMessage(exceptionCode);
    }

    public byte getExceptionCode() {
        return exceptionCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
