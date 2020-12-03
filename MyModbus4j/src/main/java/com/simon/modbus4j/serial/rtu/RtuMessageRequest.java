package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class RtuMessageRequest extends RtuMessage implements OutgoingRequestMessage, IncomingRequestMessage {
    static RtuMessageRequest createRtuMessageRequest(ByteQueue queue) throws ModbusTransportException {
        ModbusRequest request = ModbusRequest.createModbusRequest(queue);
        RtuMessageRequest rtuRequest = new RtuMessageRequest(request);

        //Check the CRC
        ModbusUtils.checkCRC(rtuRequest.modbusMessage, queue);

        //Return the data.
        return rtuRequest;
    }

    public RtuMessageRequest(ModbusRequest modbusRequest){
        super(modbusRequest);
    }

    @Override
    public boolean expectsResponse(){
        return modbusMessage.getSlaveId() != 0;
    }

    public ModbusRequest getModbusRequest(){
        return (ModbusRequest) modbusMessage;
    }
}
