package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.serial.SerialMaster;
import com.simon.modbus4j.serial.SerialPortWrapper;
import com.simon.modbus4j.serial.SerialWaitingRoomKeyFactory;
import com.simon.modbus4j.sero.ShouldNeverHappenException;
import com.simon.modbus4j.sero.messaging.MessageControl;
import com.simon.modbus4j.sero.messaging.StreamTransport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RtuMaster extends SerialMaster {

    private final Log LOG = LogFactory.getLog(RtuMaster.class);

    //Runtime fields.
    private MessageControl conn;

    public RtuMaster(SerialPortWrapper wrapper){
        super(wrapper, true);
    }

    public RtuMaster(SerialPortWrapper wrapper, boolean validateResponse){
        super(wrapper, validateResponse);
    }

    @Override
    public void init() throws ModbusInitException{
        try{
            openConnection(null);
        }catch (Exception e){
            throw new ModbusInitException(e);
        }
        initialized = true;
    }

    @Override
    protected void openConnection(MessageControl toClose) throws Exception{
        super.openConnection(toClose);

        RtuMessageParser rtuMessageParser = new RtuMessageParser(true);
        this.conn = getMessageControl();
        this.conn.start(transport, rtuMessageParser, null, new SerialWaitingRoomKeyFactory());
        if(getePoll() == null){
            ((StreamTransport) transport).start("Modbus Rtu master");
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
        //Wrap the modbus request in an rtu request.
        RtuMessageRequest rtuRequest = new RtuMessageRequest(request);

        //Send the request to get the response.
        RtuMessageResponse rtuResponse;
        try {
            rtuResponse = (RtuMessageResponse) conn.send(rtuRequest);
            if(rtuResponse == null){
                return null;
            }
            return rtuResponse.getModbusResponse();
        }catch (Exception e){
            try {
                LOG.debug("Connection may have been reset. Attempting to re-open.");
                openConnection(conn);
                rtuResponse = (RtuMessageResponse) conn.send(rtuRequest);
                if(rtuResponse == null){
                    return null;
                }
                return rtuResponse.getModbusResponse();
            }catch (Exception e2){
                closeConnection(conn);
                LOG.debug("Failed to re-connect", e);
                throw new ModbusTransportException(e2, request.getSlaveId());
            }
        }
    }

    public static long computeMessageFrameSpacing(SerialPortWrapper wrapper){
        //For Modbus Serial Spec, Message Framing rates at 19200 Baud are fixed
        if(wrapper.getBaudRate() > 19200){
            return 1750000l;//Nanoseconds
        }else {
            float charTime = computeCharacterTime(wrapper);
            return (long) (charTime * 3.5f);
        }
    }

    public static long computeCharacterSpacing(SerialPortWrapper wrapper){
        //For Modbus Serial Spec, Message Framing rates at 19200 Baud are fixed
        if(wrapper.getBaudRate() > 19200){
            return 750000l;
        }else {
            float charTime = computeCharacterTime(wrapper);
            return (long) (charTime * 1.5f);
        }
    }

    public static float computeCharacterTime(SerialPortWrapper wrapper){
        //Compute the char size
        float charBits = wrapper.getDataBits();
        switch(wrapper.getStopBits()){
            case 1:
                //Strangely this results in 0 stop bits.. in JSSC code
                break;
            case 2:
                charBits += 2f;
                break;
            case 3:
                //1.5 stop bits
                charBits += 1.5f;
                break;
            default:
                throw new ShouldNeverHappenException("Unknown stop bit size: " + wrapper.getStopBits());
        }

        if(wrapper.getParity() > 0){
            charBits += 1; //Add another if using parity
        }

        //Compute ns it takes to send one char
        //((charSize/symbols per second)) * ns per second
        return (charBits / wrapper.getBaudRate()) * 1000000000f;
    }
}
