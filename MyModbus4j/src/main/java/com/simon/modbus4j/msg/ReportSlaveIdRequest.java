package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class ReportSlaveIdRequest extends ModbusRequest {

    public ReportSlaveIdRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public void validate(Modbus modbus){

    }

    @Override
    protected void writeRequest(ByteQueue queue){

    }

    @Override
    protected void readRequest(ByteQueue queue){

    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ReportSlaveIdResponse(slaveId);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return new ReportSlaveIdResponse(slaveId, processImage.getReportSlaveIdData());
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.REPORT_SLAVE_ID;
    }
}
