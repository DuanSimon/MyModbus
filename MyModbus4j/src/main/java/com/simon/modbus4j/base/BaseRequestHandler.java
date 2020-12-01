package com.simon.modbus4j.base;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.RequestHandler;

abstract public class BaseRequestHandler implements RequestHandler {

    protected ModbusSlaveSet slave;

    public BaseRequestHandler(ModbusSlaveSet slave){
        this.slave = slave;
    }

    protected ModbusResponse handleRequestImpl(ModbusRequest request) throws ModbusTransportException {

        request.validate(slave);

        int slaveId = request.getSlaveId();

        //Check the slave id.
        if(slaveId == 0){
            //Broadcast message. Send to all process images.
            for (ProcessImage processImage :
                    slave.getProcessImages()) {
                request.handle(processImage);
            }
            return null;
        }

        //Find the process image to which to send.
        ProcessImage processImage = slave.getProcessImage(slaveId);
        if(processImage == null){
            return null;
        }
        return request.handle(processImage);
    }
}
