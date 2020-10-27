package ip.tcp;

import com.sun.org.slf4j.internal.LoggerFactory;
import exception.ModbusTransportException;
import ip.IpParameters;
import msg.ModbusRequest;
import msg.ModbusResponse;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TcpMaster extends ModbusMaster{
    private static final int RETRY_PAUSE_START = 50;
    private static final int RETRY_PAUSE_MAX = 1000;

    private final Log LOG = LogFactory.getLog(TcpMaster.class);
    private short nextTransactionId = 0;
    private final IpParameters ipParameters;
    private final boolean keepAlive;
    private final boolean autoIncrementTransactionId;

    private Socket socket;
    private Transport transport;
    private MessageControl conn;

    public TcpMaster(IpParameters params, boolean keepAlive, boolean autoIncrementTransactionId, boolean validateResponse){
        this.ipParameters = params;
        this.keepAlive = keepAlive;
        this.autoIncrementTransactionId = autoIncrementTransactionId;
    }

    public TcpMaster(IpParameters params, boolean keepAlive, boolean autoIncrementTransactionId){
        this(params, keepAlive, autoIncrementTransactionId,false);
    }

    public TcpMaster(IpParameters params, boolean keepAlive){
        this(params, keepAlive, true,false);
    }
    public void setNextTransactionId(short id){
        this.nextTransactionId = id;
    }
    protected short getNextTransactionId(){
        return nextTransactionId;
    }
    @Override
    synchronized public void init() throws ModbusInitException{
        try {
            if(keepAlive){
                openConnection();
            }
        }catch (Exception e){
            throw new ModbusInitException(e);
        }
        initialized = true;
    }
    @Override
    synchronized public void destroy(){
        closeConnection();
        initialized = false;
    }
    @Override
    synchronized public ModbusResponse sendImpl(ModbusRequest request) throws ModbusTransportException{
        try {
            if(!keepAlive){
                openConnection();
            }
            if(conn = null){
                LOG.debug("Connection null: " + ipParameters.getPort());
            }
        }catch (Exception e){
            closeConnection();
            throw new ModbusTransportException(e, request.getSlaveId());
        }
        OutgoingRequestMessage ipRequest;
        if(ipParameters.isEncapsulated()){
            ipRequest = new EncapMessageRequest(request);
        }else{
            if(autoIncrementTransactionId){
                this.nextTransactionId++;
            }
            ipRequest = new XaMessageRequest(request, getNextTransactionId());
        }
        if(LOG.isDebugEnable()){
            StringBuilder sb = new StringBuilder();
            for(byte b : Arrays.copyOfRange(ipRequest.getMessgeData(),0,ipRequest.getMessageData().length)){
                sb.append(String.format("%02X ", b));
            }
            LOG.debug("Encap Request: " + sb.toString());
        }
        IpMessageResponse ipResponse;
        LOG.debug("Sending on port: " + ipParameters.getPort());
        try {
            if(conn == null){
                LOG.debug("Connection null: " + ipParameters.getPort());
            }
            ipResponse = (IpMessageResponse) conn.send(ipRequest);
            if(ipRequest == null){
                return null;
            }
            if(LOG.isDebugEnabled()){
                StringBuilder sb = new StringBuilder();
                for(byte b : Arrays.copyOfRange(ipResponse.getMessageData(),0,ipResponse.getMesageData().length)){
                    sb.append(String.format("%02X ", b));
                }
                LOG.debug("Response: " + sb.toString());
            }
            return ipResponse.getModbusResponse();
        }catch (Exception e){
            LOG.debug("Exception: " + e.getMessage() + " " + e.getLocalizedMessage());
            if(keepAlive){
                LOG.debug("KeepAlive - reconnect!");
            }
            try {
                LOG.debug("Modbus4J: Keep_alive connection may have been reset. Attempting to re-open");
                openConnection();
                ipResponse = (IpMessageResponse) conn.send(ipRequest);
                if(ipResponse == null){
                    return null;
                }
                if(LOG.isDebugEnabled()){
                    StringBuilder sb = new StringBuilder();
                    for(byte b : Arrays.copyOfRange(ipResponse.getMessageData(),0,ipResponse.getMessgeData().length)){
                        sb.append(String.format("%02X ", b));
                    }
                    LOG.debug("Response: " + sb.toString());
                }
                return ipResponse.getModbusResponse();
            }catch (Exception e2){
                closeConnection();
                LOG.debug("Exception: " + e2.getMessage() + " " + e2.getLocalizedMessage());
                throw new ModbusTransportException(e2, request.getSlaveId());
            }
            throw new ModbusTransportException(e, request.getSlaveId());
        }finally {
            if(!keepAlive){
                closeConnection();
            }
        }
    }
    private void openConnection() throws IOException {
        closeConnection();

        int retries = getRetries();
        int retryPause = TETRY_PAUSE_START;
        while(true){
            try {
                socket = new Socket();
                socket.setSoTimeout(getTimeout());
                socket.connect(new InetSocketAddress(ipParameters.getHost(),ipParameters.getPort()), getTimeout());
                if(getePoll() != null){
                    transport = new EpollStreamTransport(socket.getInputStream(), socket.getOutputStream(), getePoll());
                }
                else{
                    transport = new StreamTransport(socket.getInputStream(), socket.getOutputStream());
                }
                break;
            }catch (IOException e){
                closeConnection();

                if(retries <= 0){
                    throw e;
                }
                retries--;

                try {
                    Thread.sleep(retryPause);
                }catch (InterruptedException e1){

                }
                retryPause *= 2;
                if(retryPause > RETRY_PAUSE_MAX){
                    retryPause = RETRY_PAUSE_MAX;
                }
            }
        }

        BaseMessageParser ipMessageParser;
        WaitingRoomKeyFactory waitingRoomKeyFactory;
        if(ipParameters.isEncapsulated()){
            ipMessageParser = new EncapMessageParser(true);
            waitingRoomKeyFactory = new EncapWaitingRoomKeyFactory();
        }else{
            ipMessageParser = new XaMessageParser(true);
            waitingRoomKeyFactory = new XaWaitingRoomKeyFactory();
        }

        conn = getMessageControl();
        conn.start(transport, ipMessageParser, null, waitingRoomKeyFactory);
        if(getePoll() == null){
            ((StreamTransport) transport).start("Modbus4J TcpMaster");
        }
    }
    private void closeConnection(){
        closeMessageControl(conn);
        try {
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            getExceptionHandler().receivedExceptiong(e);
        }

        conn = null;
        socket = null;
    }

}
