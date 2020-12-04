import com.simon.modbus4j.serial.SerialPortWrapper;

import java.io.InputStream;
import java.io.OutputStream;

public class TestSerialPortWrapper implements SerialPortWrapper {

    private String commPortId;
    private int baudRate;
    private int flowControlIn;
    private int flowControlOut;
    private int dataBits;
    private int stopBits;
    private int parity;

    public TestSerialPortWrapper(String commPortId, int baudRate, int flowControlIn, int flowcontrolOut, int dataBits, int stopBits, int parity){

        this.baudRate = baudRate;
        this.flowControlIn = flowControlIn;
        this.flowControlOut = flowcontrolOut;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    @Override
    public void close()throws Exception{

    }

    @Override
    public void open() throws Exception{

    }
    @Override
    public InputStream getInputStream(){
        return null;
    }

    @Override
    public OutputStream getOutputStream(){
        return null;
    }

    @Override
    public int getBaudRate(){
        return 0;
    }

    @Override
    public int getStopBits(){
        return 0;
    }

    @Override
    public int getParity(){
        return 0;
    }

    @Override
    public int getDataBits(){
        return 0;
    }
}
