package com.simon.modbus4j.serial;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.sero.messaging.StreamTransport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class SerialSlave extends ModbusSlaveSet {

    private final Log LOG = LogFactory.getLog(SerialSlave.class);

    //Runtime fields
    private SerialPortWrapper wrapper;
    protected StreamTransport transport;

    public SerialSlave(SerialPortWrapper wrapper){
        this.wrapper = wrapper;
    }

    @Override
    public void start() throws ModbusInitException{
        try {
            wrapper.open();
            transport = new StreamTransport(wrapper.getInputStream(), wrapper.getOutputStream());
        }catch (Exception e){
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop(){
        try {
            wrapper.close();
        }catch (Exception e){
            LOG.error(e.getMessage(), e);
        }
    }
}
