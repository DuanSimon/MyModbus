package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.serial.SerialPortWrapper;
import com.simon.modbus4j.serial.SerialSlave;
import com.simon.modbus4j.sero.messaging.MessageControl;

import java.io.IOException;

public class RtuSlave extends SerialSlave {
    //Runtime fields
    private MessageControl conn;

    public RtuSlave(SerialPortWrapper wrapper){
        super(wrapper);
    }

    @Override
    public void start() throws ModbusInitException{
        super.start();

        RtuMessageParser rtuMessageParser = new RtuMessageParser(false);
        RtuRequestHandler rtuRequestHandler = new RtuRequestHandler(this);

        conn = new MessageControl();
        conn.setExceptionHandler(getExceptionHandler());

        try {
            conn.start(transport, rtuMessageParser, rtuRequestHandler, null);
            transport.start("Modbus RTU slave");
        }catch(IOException e){
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop(){
        conn.close();
        super.stop();
    }
}
