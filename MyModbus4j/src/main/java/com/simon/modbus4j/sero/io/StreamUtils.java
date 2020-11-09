package com.simon.modbus4j.sero.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    public static void transfer(InputStream in, OutputStream out) throws IOException {
        transfer(in, out, -1);
    }

    public static void transfer(InputStream in, OutputStream out, long limit) throws IOException{
        byte[] buf = new byte[1024];
        int readcount;
        long total = 0;
        while((readcount = in.read(buf)) != -1){
            if(limit != -1){
                if(total + readcount > limit){
                    readcount = (int) (limit - total);
                }
            }

            if(readcount > 0){
                out.write(buf, 0, readcount);
            }
            total += readcount;

            if(limit != -1 && total >= limit){
                break;
            }
        }
        out.flush();
    }

    public static void transfer(InputStream in, SocketChannel out) throws IOException{
        byte[] buf = new byte[1024];
        ByteBuffer bbuf = ByteBuffer.allocate(1024);
        int len;
        while((len = in.read(buf)) != -1){
            bbuf.put(buf, 0, len);
            bbuf.flip();
            while(bbuf.remaining() > 0){
                out.write(bbuf);
            }
            bbuf.clear();
        }
    }

    public static void transfer(Reader reader, Writer writer) throws IOException{
        transfer(reader, writer, -1);
    }

    public static void transfer(Reader reader, Writer writer, long limit) throws IOException{
        char[] buf = new char[1024];
        int readcount;
        long total = 0;
        while((readcount = reader.read(buf)) != -1){
            if(limit != -1){
                if(total + readcount > limit){
                    readcount = (int) (limit - total);
                }
            }
            if(readcount > 0){
                writer.write(buf, 0, readcount);
            }

            total += readcount;
            if(limit 1= -1 && total >= limit){
                break;
            }
        }
        writer.flush();
    }

    public static byte[] read(InputStream in) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
        transfer(in, out);
        return out.toByteArray();
    }

    public static char readChar(InputStream in) throws IOException{
        return (char) in.read();
    }

    public static String readString(InputStream in, int length) throws IOException{
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(readChar(in));
        }
        return sb.toString();
    }

    public static byte readByte(InputStream in) throws IOException{
        return (byte) in.read();
    }

    public static int read4ByteSigned(InputStream in) throws IOException{
        return in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
    }

    public static int read2ByteUnsigned(InputStream in) throws IOException{
        return in.read() | (in.read() << 8);
    }

    public static short read2ByteSigned(InputStream in) throws IOException{
        return (short) (in.read() | (in.read() << 8));
    }


}
