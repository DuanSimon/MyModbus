package com.simon.modbus4j.sero.messaging;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamListener implements Runnable {
    private static final int DEFAULT_READ_DELAY = 50;

    private final InputStream in;
    private final DataConsumer consumer;
    private volatile boolean running = true;

    private int readDelay = DEFAULT_READ_DELAY;

    public InputStreamListener(InputStream in, DataConsumer consumer){
        this.in = in;
        this.consumer = consumer;
    }

    public int getReadDelay(){
        return readDelay;
    }

    public void setReadDelay(int readDelay){
        if(readDelay < 1){
            throw new IllegalArgumentException("readDelay cannot be less than one");
        }
        this.readDelay = readDelay;
    }

    public void start(String threadName){
        Thread thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop(){
        running = false;
        synchronized (this){
            notify();
        }
    }

    public void run(){
        byte[] buf = new byte[1024];
        int readcount;
        try {
            while(running){
                try {
                    if(in.available() == 0){
                        synchronized (this){
                            try {
                                wait(readDelay);
                            }catch (InterruptedException e){

                            }
                        }
                        continue;
                    }

                    readcount = in.read(buf);
                    consumer.data(buf, readcount);
                }catch (IOException e){
                    consumer.handleIOException(e);
                    if(StringUtils.equals(e.getMessage(), "Stream close.")){
                        break;
                    }
                    if(StringUtils.contains(e.getMessage(), "nativeavailable")){
                        break;
                    }
                }
            }
        }finally {
            running = false;
        }
    }
}
