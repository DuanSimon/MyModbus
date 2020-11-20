package com.simon.modbus4j.sero.messaging;

import com.simon.modbus4j.sero.epoll.Modbus4JInputStreamCallback;

import java.io.OutputStream;

public class EpollStreamTransport implements Transport {
    private final OutputStream out;
    private final InputStream in;
    private final InputStreamEPollWrapper epoll;

    public EpollStreamTransport(InputStream in, OutputStream out, InputStreamEPollWrapper epoll){
        this.out = out;
        this.in = in;
        this.epoll = epoll;
    }

    @Override
    public void setConsumer(final DataConsumer consumer){
        epoll.add(in, new Modbus4JInputStreamCallback() {
            @Override
            public void terminated(){
                removeConsumer();
            }

            @Override
            public void ioException(IOException e){
                consumer.handleIOException(e);
            }

            @Override
            public void input(byte[] buf, int len){
                consumer.data(buf, len);
            }

            @Override
            public void closed(){
                removeConsumer();
            }
        });
    }

    @Override
    public void removeConsumer(){
        epoll.remove(in);
    }

    @Override
    public void write(byte[] data) throws IOException{
        out.write(data);
        out.flush();
    }

    @Override
    public void write(byte[] data, int len) throws IOException{
        out.write(data, 0, len);
        out.flush();
    }
}
