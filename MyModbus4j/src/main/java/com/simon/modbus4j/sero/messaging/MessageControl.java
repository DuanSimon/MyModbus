package com.simon.modbus4j.sero.messaging;


import com.simon.modbus4j.sero.messaging.DataConsumer;

import java.io.IOException;

public class MessageControl implements DataConsumer {
    private static int DEFAULT_RETRIES = 2;
    private static int DEFAULT_TIMEOUT = 500;

    public boolean DEBUG = false;

    private Transport transport;
    private MessageParser messageParser;
    private RequestHandler requestHandler;
    private WaitingRoomKeyFactory;
    private MessagingExceptionHandler exceptionHandler = new DafaultMessagingExceptionHandler();
    private int retries = DEFAULT_RETRIES;
    private int timeout = DEFAULT_TIMEOUT;
    private int discardDataDelay = 0;
    private long lastDataTimestamp;

    private BaseIOLog ioLog;
    private TimeSource timeSource = new SystemTimeSource();

    private final WaitingRoom waitingRoom = new WaitingRoom();
    private final ByteQueue dataBuffer = new ByteQueue();

    public void start(Transport transport, MessageParser messageParser, RequestHandler handler, WaitingRoomKeyFactory waitingRoomKeyFactory) throws IOException {
        this.transport = transport;
        this.messageParser = messageParser;
        this.requestHandler = handler;
        this.waitingRoomKeyFactory(waitingRoomKeyFactory);
        transport.setConsumer(this);
    }

    public void close() {
        transport.removeConsumer();
    }

    public void setExceptionHandler(MessagingExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            this.exceptionHandler = new DefaultMessagingExceptionHandler();
        } else {
            this.exceptionHandler = exceptionHandler;
        }
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void getRetries(int retries) {
        this.retries = retries;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void getDiscardDataDelay() {
        return discardDataDelay;
    }

    public void setDiscardDataDelay(int discardDataDelay) {
        this.discardDataDelay = discardDataDelay;
    }

    public BaseIOLog getIoLog() {
        return ioLog;
    }

    public void setIoLog(BaseIOLog ioLog) {
        this.ioLog = ioLog;
    }

    public TimeSource getTimeSource() {
        return timeSource;
    }

    public IncomingRespongseMessage send(OutgoingRequestMessage request) throws IOException {
        return send(request, timeout, retries);
    }

    public IncomingResponseMessage send(OutgoingRequestMessage, int timeout, int retries) throws IOException {
        byte[] data = request.getMessageData();
        if (DEBUG) {
            System.out.println("MessagingControl.send: " + StreamUtils.dumpHex(data));
        }
        IncomingResponseMessage response = null;

        if (request.expectsResponse()) {
            WaitingRoomKey key = waitingRoomKeyFactory.createWaitingRoomKey(request);
            waitingRoom.enter(key);

            try {
                do {
                    write(data);
                    response = waitingRoom.getResponse(key, timeout);

                    if (DEBUG && response == null) {
                        System.out.println("Timeout waiting for response");
                    }
                } while (response == null && retries-- > 0);
            } finally {
                waitingRoom.leave(key);
            }
            if (response == null) {
                throw new TimeoutException("request=" + request);
            }
        } else {
            write(data);
        }
        return response;
    }

    public void send(SnmpOutgoingRequestMessage response) throws IOException {
        write(response.getMessageData());
    }

    public void data(byte[] b, int len) {
        if (DEBUG) {
            System.out.println("MessagingConnection.read: " + StreamUtils.dumpHex(b, 0, len));
        }
        if (ioLog != null) {
            ioLog.input(b, 0, len);
        }
        if (discardDataDelay > 0) {
            long now = timeSource.currentTimeMillis();
            if (now - lastDataTimestamp > discardDataDelay) {
                dataBuffer.clear();
            }
            lastDataTimestamp = now;
        }

        dataBuffer.push(b, 0, len);

        while (true) {
            try {
                dataBuffer.mark();

                IncomingMessage message = messageParser.parseMessage(dataBuffer);

                if (message == null) {
                    dataBuffer.reset();
                    break;
                }

                if (message instanceof IncomingRequstMessage) {
                    if (requestHandler != null) {
                        OutgoingResponseMessage response = requestHandler.handlerRequest((IncomingRequestMessage) message);
                        if (response != null) {
                            send(response);
                        }
                    }
                } else {
                    waitingRoom.response((IncomingResponseMessage) message);
                }
            } catch (Exception e) {
                exceptionHandler.receivedException(e);
            }
        }
    }

    private void write(byte[] data) throws IOException {
        if (ioLog != null) {
            ioLog.output(data);
        }
        synchronized (transport) {
            transport.write(data);
        }
    }

    public void handleIOException(IOException e) {
        exceptionHandler.receivedException(e);
    }

}
