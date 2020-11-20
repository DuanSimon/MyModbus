package com.simon.modbus4j.sero.io;

import java.io.Writer;

public class NullWriter extends Writer {

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException{

    }

    @Override
    public void flush() throws IOException{

    }

    @Override
    public void close() throws IOException{

    }
}
