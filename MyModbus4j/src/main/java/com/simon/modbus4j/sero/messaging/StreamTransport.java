package com.simon.modbus4j.sero.messaging;

import java.io.InputStream;

public class StreamTransport implements Transport, Runnable {
    protected OutputStream out;
    protected InputStream in;
    private  InputStreamListener listener;

    public StreamTransport(InputStream in, OutputStream out){
        this.out = out;
        this.in = in;
    }

    public void setReadDelay(int readDelay){
        if(listener != null){
            listener.setReadDelay(readDelay);
        }
    }

    public void start(String threadname){
        listener.start(threadname);
    }

    public void stop(){
        listener.stop();
    }

    public void run(){
        listener.run();
    }

    public void setConsumer(DataConsumer consumer){
        listener = new InputStreamListener(in, consumer);
    }

    public void removeConsumer(){
        listener.stop();
        listener = null;
    }

    public void write(byte[] data) throws IOException{
        out.write(data);
        out.flush();
    }

    public void write(byte[] data, int len) throws IOException{
        out.write(data, 0, len);
        out.flush();
    }
}
