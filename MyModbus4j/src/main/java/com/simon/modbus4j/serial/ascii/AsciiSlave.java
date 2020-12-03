package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.serial.SerialPortWrapper;
import com.simon.modbus4j.serial.SerialSlave;
import com.simon.modbus4j.sero.messaging.MessageControl;

import java.io.IOException;

public class AsciiSlave extends SerialSlave {
    private MessageControl conn;

    public AsciiSlave(SerialPortWrapper wrapper){
        super(wrapper);
    }

    @Override
    public void start() throws ModbusInitException{
        super.start();

        AsciiMessageParser asciiMessageParser = new AsciiMessageParser(false);
        AsciiRequestHandler asciiRequestHandler = new AsciiRequestHandler(this);

        conn = new MessageControl();
        conn.setExceptionHandler(getExceptionHandler());

        try{
            conn.start(transport, asciiMessageParser, asciiRequestHandler, null);
            transport.start("Modbus ASCII slave");
        }catch (IOException e){
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop(){
        conn.close();
        super.stop();
    }
}
