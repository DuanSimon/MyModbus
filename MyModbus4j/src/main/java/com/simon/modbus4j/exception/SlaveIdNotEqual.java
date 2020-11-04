package com.simon.modbus4j.exception;

public class SlaveIdNotEqual extends ModbusTransportException {
    private static final long serialVersionUID = -1;

    public SlaveIdNotEqual(int requestSlaveId, int responseSlaveId){
        super("Response slave id different from requested id", requestSlaveId);
    }
}
