package com.simon.modbus4j.sero.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTransportCharSpaced extends StreamTransport {

    private final long charSpacing;

    public StreamTransportCharSpaced(InputStream in, OutputStream out, long charSpacing){
        super(in, out);
        this.charSpacing = charSpacing;
    }

    @Override
    public void write(byte[] data) throws IOException {
        try {
            long waited = 0,writeStart,writeEnd,WaitRemaining;
            for (byte b :
                    data) {
                writeStart = System.nanoTime();
                out.write(b);
                writeEnd =System.nanoTime();
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
