package com.simon.modbus4j.sero.messaging;

import com.simon.modbus4j.sero.epoll.InputStreamEPollWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EpollStreamTransportCharSpaced extends EpollStreamTransport {

    private final long charSpacing;
    private final OutputStream out;

    public EpollStreamTransportCharSpaced(InputStream in, OutputStream out, InputStreamEPollWrapper epoll, long charSpacing){
        super(in, out, epoll);
        this.out = out;
        this.charSpacing = charSpacing;
    }

    @Override
    public void write(byte[] data) throws IOException {

        try {
            long waited = 0, writeStart, writeEnd, waitRemaining;
            for (byte b :
                    data) {
                writeStart = System.nanoTime();
                out.write(b);
                writeEnd = System.nanoTime();
                waited = writeEnd - writeStart;
                if(waited < this.charSpacing){
                    waitRemaining = this.charSpacing - waited;
                    Thread.sleep(waitRemaining / 1000000, (int)(waitRemaining % 1000000));
                }
            }
        }catch (Exception e){
            throw new IOException(e);
        }
        out.flush();
    }

    public void write(byte[] data, int len) throws IOException{
        try {
            long waited = 0,writeStart,writeEnd,waitRemaining;
            for (int i = 0; i < len; i++) {
                writeStart = System.nanoTime();
                out.write(data[i]);
                writeEnd = System.nanoTime();
                waited = writeEnd - writeStart;
                if(waited < this.charSpacing){
                    waitRemaining = this.charSpacing - waited;
                    Thread.sleep(waitRemaining / 1000000, (int)(waitRemaining % 1000000));
                }
            }
        }catch (Exception e){
            throw new IOException(e);
        }
        out.flush();
    }
}
