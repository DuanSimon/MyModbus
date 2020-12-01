package com.simon.modbus4j.ip.tcp;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.ip.encap.EncapMessageParser;
import com.simon.modbus4j.ip.encap.EncapRequestHandler;
import com.simon.modbus4j.ip.xa.XaMessageParser;
import com.simon.modbus4j.ip.xa.XaRequestHandler;
import com.simon.modbus4j.sero.messaging.MessageControl;
import com.simon.modbus4j.sero.messaging.TestableTransport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSlave extends ModbusSlaveSet {
    private final int port;
    final boolean encapsulated;

    private ServerSocket serverSocket;
    final ExecutorService executorService;
    final List<TcpConnectionHandler> listConnections = new ArrayList<>();

    public TcpSlave(boolean encapsulated) {
        this(ModbusUtils.TCP_PORT, encapsulated);
    }

    public TcpSlave(int port, boolean encapsulated) {
        this.port = port;
        this.encapsulated = encapsulated;
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void start() throws ModbusInitException {
        try {
            serverSocket = new ServerSocket(port);

            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                TcpConnectionHandler handler = new TcpConnectionHandler(socket);
                executorService.execute(handler);
                synchronized (listConnections) {
                    listConnections.add(handler);
                }
            }
        } catch (IOException e) {
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            getExceptionHandler().receiveException(e);
        }

        synchronized (listConnections) {
            for (TcpConnectionHandler tch :
                    listConnections) {
                tch.kill();
            }
            listConnections.clear();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUtil.SECONDS);
        } catch (InterruptedException e) {
            getExceptionHandler().receiveException(e);
        }
    }

    class TcpConnectionHandler implements Runnable {
        private final Socket socket;
        private TestableTransport transport;
        private MessageControl conn;

        TcpConnectionHandler(Socket socket) throws ModbusInitException {
            this.socket = socket;
            try {
                transport = new TestableTransport(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                throw new ModbusInitException(e);
            }
        }

        @Override
        public void run() {
            BaseMessageParser messageParser;
            BaseRequestHandler requestHandler;

            if (encapsulated) {
                messageParser = new EncapMessageParser(false);
                requestHandler = new EncapRequestHandler(TcpSlave.this);
            } else {
                messageParser = new XaMessageParser(false);
                requestHandler = new XaRequestHandler(TcpSlave.this);
            }

            conn = new MessageControl();
            conn.setExceptionHandler(getExceptionHandler());

            try {
                conn.start(transport, messageParser, requestHandler, null);
                executorService.execute(transport);
            } catch (IOException e) {
                getExceptionHandler().receivedException(new ModbusInitException(e));
            }

            while (true) {
                try {
                    transport.testInputStream();
                } catch (IOException e) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }

            conn.close();
            kill();
            synchronized (listConnections) {
                return listConnections.remove(this);
            }
        }

        void kill() {
            try {
                socket.close();
            } catch (IOException e) {
                getExceptionHandler().receivedException(new ModbusInitException(e));
            }
        }
    }
}
