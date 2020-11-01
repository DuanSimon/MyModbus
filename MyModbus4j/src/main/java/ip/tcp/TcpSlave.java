package ip.tcp;

import base.ModbusUtils;
import exception.ModbusInitException;
import sun.rmi.transport.tcp.TCPConnection;
import sun.security.krb5.internal.EncAPRepPart;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpSlave extends ModbusSlaveSet {
    private final int port;
    final boolean encapsulated;

    private ServerSocket serverSocket;
    final ExecutorService executorService;
    final List<TcpConnectionHandler> listConnections = new ArrayList<>();

    public TcpSlave(boolean encapsulated){
        this(ModbusUtils.TCP_PORT, encapsulated);
    }

    public TcpSlave(int port, boolean encapsulated){
        this.port = port;
        this.encapsulated = encapsulated;
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void start() throws ModbusInitException {
        try {
            serverSocket = new ServerSocket(port);

            Socket socket;
            while(true){
                socket = serverSocket.accept();
                TcpConnectionHandler handler = new TcpConnectionHandler(socket);
                executorService.execute(handler);
                synchronized(listConnections){
                    listConnections.add(handler);
                }
            }
        }catch (IOException e){
            throw new ModbusInitException(e);
        }
    }

    @Override
    public void stop(){
        try {
            serverSocket.close();
        }catch (IOException e){
            getExceptionHandler().receiveException(e);
        }

        synchronized (listConnections){
            for (TcpConnectionHandler tch :
                    listConnections) {
                tch.kill();
            }
            listConnections.clear();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUtil.SECONDS);
        }catch (InterruptedException e){
            getExceptionHandler().receiveException(e);
        }
    }
    class TcpConnectionHandler implements Runnable{
        private final Socket socket;
        private TestableTransport transport;
        private MessageControl conn;

        TcpConnectionHandler(Socket socket) throws ModbusInitException{
            this.socket = socket;
            try {
                transport = new TestableTransport(socket.getInputStream(), socket.getOutputStream());
            }catch (IOException e){
                throw new ModbusInitException(e);
            }
        }

        @Override
        public void run(){
            BaseMessageParser messageParser;
            BaseRequestHandler requestHandler;

            if(encapsulated){
                messageParser = new EncapMessageParser(false);
                requestHandler = new EncapRequestHandler(TcpSlave.this);
            }else{
                messageParser = new XamessageParser(false);
                requestHandler = new XaRequestHandler(TcpSlave.this);
            }

            conn = new MessageControl();
            conn.setExceptionHandler(getExceptionHandler());

            try {
                conn.start(transport, messageParser, requestHandler, null);
                executorService.execute(transport);
            }catch (IOException e){
                getExceptionHandler().receivedException(new ModbusInitException(e));
            }

            while(true){
                try {
                    transport.testInputStream();
                }catch (IOException e){
                    break;
                }
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){

                }
            }

            conn.close();
            kill();
            synchronized (listConnections){
                return listConnections.remove(this);
            }
        }

        void kill(){
            try {
                socket.close();
            }catch (IOException e){
                getExceptionHandler().recervedException(new ModbusInitException(e));
            }
        }
    }
}
