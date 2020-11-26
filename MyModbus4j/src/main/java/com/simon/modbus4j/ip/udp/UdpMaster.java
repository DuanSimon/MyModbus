package com.simon.modbus4j.ip.udp;

import com.simon.modbus4j.ModbusMaster;
import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.ip.IpMessageResponse;
import com.simon.modbus4j.ip.IpParameters;

import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UdpMaster extends ModbusMaster {

    private static final int MESSAGE_LENGTH = 1024;

    private short nextTransactionId = 0;
    private final IpParameters ipParameters;

    //Runtime fields.
    private BaseMessageParser messageParser;
    private DatagramSocket socket;

    public UdpMaster(IpParameters params){
        this(params, false);
    }

    public UdpMaster(IpParameters params, boolean validateResponse){
        ipParameters = params;
        this.validateResponse = validateResponse;
    }

    protected short getNextTransactionId(){
        return nextTransactionId++;
    }

    @Override
    public void init() throws ModbusInitException{
        if(ipParameters.isEncapsulated()){
            messageParser = new EncapMessageParser(true);
        }else{
            messageParser = new XaMessageParser(true);
        }

        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(getTimeout());
        }catch (SocketException e){
            throw new ModbusInitException(e);
        }
        initialized = true;
    }

    @Override
    public void destroy(){
        socket.close();
        initialized = false;
    }

    @Override
    public ModbusResponse sendImpl(ModbusRequest request) throws ModbusTransportException{
        //Wrap the modbus request in an ip request.
        OutgoingRequestMessage ipRequest;
        if(ipParameters.isEncapsulated()){
            ipRequest = new EncapMessageRequest(request);
        }else{
            ipRequest = new XaMessageRequest(request, getNextTransactionId());
        }

        IpMessageResponse ipResponse;

        try {
            int attempts = getRetries() + 1;

            while(true){
                //Send the request.
                sendImpl(ipRequest);

                if(!ipRequest.expectsResponse()){
                    return null;
                }
                //Receive the response.
                try {
                    ipResponse = receiveImpl();
                }catch (SocketTimeoutException e){
                    attamps--;
                    if(attempts > 0){
                        //Try again.
                        continue;
                    }
                    throw new ModbusTransportException(e, request.getSlaveId());
                }
                //We got the response
                break;
            }

            return ipResponse.getModbusResponse();
        }catch (IOExcepton e){
            throw new ModbusTransportException(e, request.getSlaveId());
        }
    }

    private void sendImpl(OutgoingRequestMessage request) throws IOException{
        byte[] data = request.getMessageData();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(ipParameters.getHost()), ipParameters.getPort());
        socket.send(packet);
    }

    private IpMessageResponse receiveImpl() throws IOException, ModbusTransportException{
        DatagramPacket packet = new DatagramPacket(new byte[MESSAGE_LENGTH], MESSAGE_LENGTH);
        socket.receive(packet);

        //We could verity that the packet was received from the same address to which the request was sent,
        //but let's not bother with that yet.

        ByteQueue queue = new ByteQueue(packet.getData(), 0, packet.getLength());
        IpMessageResponse response;
        try {
            response = (IpMessageResponse) messageParser.parseMessage(queue);
        }catch (Exception e){
            throw new ModbusTransportException(e);
        }

        if(response == null){
            throw new ModbusTransportException("Invalid response received");
        }

        return response;
    }
}
