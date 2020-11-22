package com.simon.modbus4j.serial;

import com.simon.modbus4j.ModbusMaster;
import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.sero.messaging.EpollStreamTransport;
import com.simon.modbus4j.sero.messaging.StreamTransport;
import com.simon.modbus4j.sero.messaging.Transport;

abstract public class SerialMaster extends ModbusMaster {
    private static final int RETRY_PAUSE_START = 50;
    private static final int RETRY_PAUSE_MAX = 1000;

    private final Log LOG = LogFactory.getLog(SerialMaster.class);

    //Runtime fields.
    protected boolean serialPortOpen;
    protected SerialPortWrapper wrapper;
    protected Transport transport;

    public SerialMaster(SerialPortWrapper wrapper){
        this(wrapper, true);
    }

    public SerialMaster(SerialPortWrapper wrapper, boolean validateResponse){
        this.wrapper = wrapper;
        this.validateResponse = validateResponse;
    }

    @Override
    public void init() throws ModbusInitException{
        try{
            this.openConnection(null);
        }catch (Exception e){
            throw new ModbusInitException(e);
        }
    }

    protected void openConnection(MessageControl toClose) throws Exception{
        //Make sure any existing connection is closed.
        cloneConnection(toClose);

        //Try 'retries' times to get the socket open.
        int retries = getRetries();
        int retryPause = RETRY_PAUSE_START;
        while(true){
            try {
                this.wrapper.open();
                this.serialPortOpen = true;
                if(getePoll() != null){
                    transport = new EpollStreamTransport(wrapper.getInputStream(), wrapper.getOutputStream(), getePoll());
                }else {
                    transport = new StreamTransport(wrapper.getInputStream(),wrapper.getOutputStream());
                }
                break;
            }catch (Exception e){
                //Ensure port is closed before we try to reopen or bail out
                close();

                if(retries <= 0){
                    throw e;
                }
                retries--;

                //Pause for a bit
                try {
                    Thread.sleep(retryPause);
                }catch (InterruptedException e1){
                    //ignore
                }
                retryPause *= 2;
                if(retryPause > RETRY_PAUSE_MAX){
                    retryPause = RETRY_PAUSE_MAX;
                }
            }
        }
    }

    protected void closeConnection(MessageControl conn){
        closeMessageControl(conn);
        try {
            if(serialPortOpen){
                wrapper.close();
                serialPortOpen = false;
            }
        }catch (Exception e){
            getExceptionHandler().receivedException(e);
        }

        transport = null;
    }

    public void close(){
        try {
            wrapper.close();
        }catch (Exception e){
            LOG.error(e.getMessage(), e);
        }
    }
}
