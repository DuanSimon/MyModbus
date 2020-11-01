public interface ProcessImage {
    int getSlaveId();

    boolean getCoil(int offset) throws IllegalDataAddressException;

    void setCoil(int offset, boolean value);

    void writeCoil(int offset, boolean value) throws IllegalDataAddressException;

    boolean getInput(int offset) throws IllegalDataAddressException;

    void setInput(int offset, boolean value);

    void getHoldingRegister(int offset) throws IllegalDataAddressException;

    void setHoldingRegister(int offset, short value);

    void writeHoldingRegister(int offset, short value) throws IllegalDataAddressException;

    short getInputRegister(int offset) throws IllegalDataAddressException;

    void setInputRegister(int offset, short value);

    byte getExceptionStatus();

    byte[] getReportSlaveIdData();

}
