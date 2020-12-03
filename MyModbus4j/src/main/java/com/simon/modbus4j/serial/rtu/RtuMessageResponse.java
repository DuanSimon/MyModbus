package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingResponseMessage;
import com.simon.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class RtuMessageResponse extends RtuMessage implements OutgoingResponseMessage, IncomingResponseMessage {
    static RtuMessageResponse createRtuMessageResponse(ByteQueue queue) throws ModbusTransportException{
        ModbusResponse response = ModbusResponse.createModbusResponse(queue);
        RtuMessageResponse rtuResponse = new RtuMessageResponse(response);

        //Check the CRC
        ModbusUtils.checkCRC(rtuResponse.modbusMessage, queue);

        //Return the data.
        return rtuResponse;
    }

    public RtuMessageResponse(ModbusResponse modbusResponse){
        super(modbusResponse);
    }

    public ModbusResponse getModbusResponse(){
        return (ModbusResponse) modbusMessage;
    }
}
