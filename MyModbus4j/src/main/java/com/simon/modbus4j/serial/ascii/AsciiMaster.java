package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.serial.SerialMaster;
import com.simon.modbus4j.serial.SerialPortWrapper;
import com.simon.modbus4j.serial.SerialWaitingRoomKeyFactory;
import com.simon.modbus4j.sero.messaging.MessageControl;
import com.simon.modbus4j.sero.messaging.StreamTransport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AsciiMaster extends SerialMaster {
    private final Log LOG = LogFactory.getLog(SerialMaster.class);

    private MessageControl conn;

    public AsciiMaster(SerialPortWrapper wrapper){
        super(wrapper, true);
    }

    public AsciiMaster(SerialPortWrapper wrapper, boolean validateResponse){
        super(wrapper, validateResponse);
    }

    @Override
    public void init() throws ModbusInitException{
        try {
            openConnection(null);
        }catch (Exception e){
            throw new ModbusInitException(e);
        }
        initialized = true;
    }

    @Override
    protected void openConnection(MessageControl toClose) throws Exception{
        super.openConnection(toClose);
        AsciiMessageParser asciiMessageParser = new AsciiMessageParser(true);
        this.conn = getMessageControl();
        this.conn.start(transport, asciiMessageParser, null, new SerialWaitingRoomKeyFactory());
        if(getePoll() == null){
            ((StreamTransport) transport).start("Modbus ASCII master");
        }
    }

    @Override
    public void destroy(){
        closeMessageControl(conn);
        super.close();
        initialized = false;
    }

    @Override
    public ModbusResponse sendImpl(ModbusRequest request) throws ModbusTransportException{
        //Wrap the modbus request in an asciii request.
        AsciiMessageRequest asciiRequest = new AsciiMessageRequest(request);

        //Send the request to get the response.
        AsciiMessageResponse asciiResponse;
        try {
            asciiResponse = (AsciiMessageResponse) conn.send(asciiRequest);
            if(asciiResponse == null){
                return null;
            }
            return asciiResponse.getModbusResponse();
        }catch (Exception e){
            try {
                LOG.debug("Connection may have been reset. Attempting to re-open.");
                openConnection(conn);
                asciiResponse = (AsciiMessageResponse) conn.send(asciiRequest);
                if(asciiResponse == null){
                    return null;
                }
                return asciiResponse.getModbusResponse();
            }catch (Exception e2){
                closeConnection(conn);
                LOG.debug("Failed to re-connect", e);
                throw new ModbusTransportException(e2, request.getSlaveId());
            }
        }
    }
}
