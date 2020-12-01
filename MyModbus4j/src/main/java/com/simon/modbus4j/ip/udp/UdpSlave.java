package com.simon.modbus4j.ip.udp;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.ip.encap.EncapMessageParser;
import com.simon.modbus4j.ip.encap.EncapRequestHandler;
import com.simon.modbus4j.ip.xa.XaMessageParser;
import com.simon.modbus4j.ip.xa.XaRequestHandler;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UdpSlave extends ModbusSlaveSet {
    //Configuration fields
    private final int port;

    //Runtime fields.
    DatagramSocket datagramSocket;
    private final ExecutorService executorService;
    final BaseMessageParser messageParser;
    final BaseRequestHandler requestHandler;

    public UdpSlave(boolean encapsulated){
        this(ModbusUtils.TCP_PORT, encapsulated);
    }

    public UdpSlave(int port, boolean encapsulated){
        this.port = port;

        if(encapsulated){
            messageParser = new EncapMessageParser(false);
            requestHandler = new EncapRequestHandler(this);
        }else{
            messageParser = new XaMessageParser(false);
            requestHandler = new XaRequestHandler(this);
        }

        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void start() throws ModbusInitException{
        try {
            datagramSocket = new DatagramSocket(port);

            DatagramPacket datagramPacket;
            while(true){
                datagramPacket = new DatagramPacket(new byte[1028], 1028);
                datagramSocket.execute(handler);

                UdpConnectionHandler handler = new UdpConnectionHandler(datagramPacket);
                executeService.execute(handler);
            }
        }catch (IOException e){
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop(){
        //Close the socket first to prevent new message.
        datagramSocket.close();

        //Close the executor service.
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        }catch (InterruptException e){
            getExceptionHandler().receivedException(e);
        }
    }

    class UdpConnectionHandler implements Runnable{
        private final DatagramPacket requestPacket;

        UdpConnectionHandler(DatagramPacket requestPacket){
            this.requestPacket = requestPacket;
        }

        public void run(){
            try {
                ByteQueue requestQueue = new ByteQueue(requestPacket.getData(), 0, requestPacket.getLength());

                //Parse the request data and get the response.
                IncomingMessage request = messageParser.parseMessage(requestQueue);
                OutgoingResponseMessage response = requestHandler.handleRequest((IncomingRequestMessage) request);

                if(response == null){
                    return;
                }

                //Create a response packet.
                byte[] responseData = response.getMessageData();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort());

                //Send the response back.
                datagramSocket.send(responsePacket);
            }catch (Exception e){
                getExceptionHandler().receivedException(e);
            }
        }
    }
}