package com.simon.modbus4j;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.base.RangeAndOffset;
import com.simon.modbus4j.code.RegisterRange;
import com.simon.modbus4j.exception.IllegalDataAddressException;
import com.simon.modbus4j.exception.ModbusIdException;
import com.simon.modbus4j.locator.BaseLocator;
import com.simon.modbus4j.locator.NumericLocator;
import com.simon.modbus4j.locator.StringLocator;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicProcessImage implements ProcessImage {
    private final int slaveId;
    private boolean allowInvalidAddress = false;
    private short invalidAddressValue = 0;

    private final Map<Integer, Boolean> coils = new HashMap<>();
    private final Map<Integer, Boolean> inputs = new HashMap<>();
    private final Map<Integer, Short> holdingRegisters = new HashMap<>();
    private final Map<Integer, Short> inputRegisters = new HashMap<>();
    private final List<ProcessImageListener> writeListeners = new ArrayList<>();
    private byte exceptionStatus;

    public BasicProcessImage(int slaveId) {
        ModbusUtils.validateSlaveId(slaveId, false);
        this.slaveId = slaveId;
    }

    @Override
    public int getSlaveId() {
        return slaveId;
    }

    public synchronized void addListener(ProcessImageListener l) {
        writeListeners.add(l);
    }

    public synchronized void removeListener(ProcessImageListener l) {
        writeListeners.remove(l);
    }

    public boolean isAllowInvalidAddress() {
        return allowInvalidAddress;
    }

    public void setAllowInvalidAddress(boolean allowInvalidAddress) {
        this.allowInvalidAddress = allowInvalidAddress;
    }

    public short getInvalidAddressValue() {
        return invalidAddressValue;
    }

    public void setInvalidAddressValue(short invalidAddressValue) {
        this.invalidAddressValue = invalidAddressValue;
    }

    public void setExceptionStatus(byte exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public void setBinary(int registerId, boolean value) {
        RangeAndOffset rao = new RangeAndOffset(registerId);
        setBinary(rao.getRange(), rao.getOffset(), value);
    }

    public void setBinary(int range, int offset, boolean value) {
        if (range == RegisterRange.COIL_STATUS) {
            setCoil(offset, value);
        } else if (range == RegisterRange.INPUT_STATUS) {
            setInput(offset, value);
        } else {
            throw new ModbusIdException("Invalid range to set binary: " + range);
        }
    }

    public synchronized void setNumeric(int registerId, int dataType, Number value) {
        RangeAndOffset rao = new RangeAndOffset(registerId);
        setNumeric(rao.getRange(), rao.getOffset(), dataType, value);
    }

    public synchronized void setNumeric(int range, int offset, int dataType, Number value) {
        short[] registers = new NumericLocator(slaveId, range, offset, dataType).valueToShorts(value);

        if (range == RegisterRange.HOLDING_REGISTER) {
            setHoldingRegister(offset, registers);
        } else if (range == RegisterRange.INPUT_REGISTER) {
            setInputRegister(offset, registers);
        } else {
            throw new ModbusIdException("Invalid range to set register: " + range);
        }
    }

    public synchronized void setString(int range, int offset, int dataType, int registerCount, String s) {
        setString(range, offset, dataType, registerCount, StringLocator.ASCII, s);
    }

    public synchronized void setString(int range, int offset, int dataType, int registerCount, Charset charset, String s) {
        short[] registers = new StringLocator(slaveId, range, offset, dataType, registerCount, charset).valueToShorts(s);

        if (range == RegisterRange.HOLDING_REGISTER) {
            setHoldingRegister(offset, registers);
        } else if (range == RegisterRange.INPUT_REGISTER) {
            setInputRegister(offset, registers);
        } else {
            throw new ModbusIdException("Invalid range to set register: " + range);
        }
    }

    public synchronized void setHoldingRegister(int offset, short[] registers) {
        validateOffset(offset);
        for (int i = 0; i < registers.length; i++) {
            setHoldingRegister(offset + i, registers[i]);
        }
    }

    public synchronized void setInputRegister(int offset, short[] registers) {
        validateOffset(offset);
        for (int i = 0; i < registers.length; i++) {
            setInputRegister(offset + i, registers[i]);
        }
    }

    public synchronized void setBit(int range, int offset, int bit, boolean value) {
        if (range == RegisterRange.HOLDING_REGISTER) {
            setHoldingRegisterBit(offset, bit, value);
        } else if (range == RegisterRange.INPUT_REGISTER) {
            setInputRegisterBit(offset, bit, value);
        } else {
            throw new ModbusIdException("Invalid rnage to set register: " + range);
        }
    }

    public synchronized void setHoldingRegisterBit(int offset, int bit, boolean value) {
        validateBit(bit);
        short s;
        try {
            s = getHoldingRegister(offset);
        } catch (IllegalDataAddressException e) {
            s = 0;
        }
        setHoldingRegister(offset, setBit(s, bit, value));
    }

    public synchronized void setInputRegisterBit(int offset, int bit, boolean value) {
        validateBit(bit);
        short s;
        try {
            s = getInputRegister(offset);
        } catch (IllegalDataAddressException e) {
            s = 0;
        }
        setInputRegister(offset, setBit(s, bit, value));
    }

    public boolean getBit(int range, int offset, int bit) throws IllegalDataAddressException {
        if (range == RegisterRange.HOLDING_REGISTER) {
            return getHoldingRegisterBit(offset, bit);
        }
        if (range == RegisterRange.INPUT_REGISTER) {
            return getInputRegisterBit(offset, bit);
        }
        throw new ModbusIdException("Invalid range to get register: " + range);
    }

    public boolean getHoldingRegisterBit(int offset, int bit) throws IllegalDataAddressException {
        validateBit(bit);
        return getBit(getHoldingRegister(offset), bit);
    }

    public boolean getInputRegisterBit(int offset, int bit) throws IllegalDataAddressException {
        validateBit(bit);
        return getBit(getInputRegister(offset), bit);
    }

    public Number getNumeric(int range, int offset, int dataType) throws IllegalDataAddressException {
        return getRegister(new NumericLocator(slaveId, range, offset, dataType));
    }

    public String getString(int range, int offset, int dataType, int registerCount) throws IllegalDataAddressException {
        return getRegister(new StringLocator(slaveId, range, offset, dataType, registerCount, null));
    }

    public String getString(int range, int offset, int dataType, int registerCount, Charset charset) throws IllegalDataAddressException {
        return getRegister(new StringLocator(slaveId, range, offset, dataType, registerCount, charset));
    }

    public synchronized <T> T getRegister(BaseLocator<T> locator) throws IllegalDataAddressException {
        int words = locator.getRegisterCount();
        byte[] b = new byte[locator.getRegisterCount() * 2];
        for (int i = 0; i < words; i++) {
            short s;
            if (locator.getRange() == RegisterRange.INPUT_REGISTER) {
                s = getInputRegister(locator.getOffset() + i);
            } else if (locator.getRange() == RegisterRange.HOLDING_REGISTER) {
                s = getHoldingRegister(locator.getOffset() + i);
            } else if (allowInvalidAddress) {
                s = invalidAddressValue;
            } else {
                throw new IllegalDataAddressException();
            }
            b[i * 2] = ModbusUtils.toByte(s, true);
            b[i * 2 + 1] = ModbusUtils.toByte(s, false);
        }
        return locator.bytesToValueRealOffset(b, 0);
    }

    @Override
    public synchronized boolean getCoil(int offset) throws IllegalDataAddressException {
        return getBoolean(offset, coils);
    }

    @Override
    public synchronized void setCoil(int offset, boolean value) {
        validateOffset(offset);
        coils.put(offset, value);
    }

    @Override
    public synchronized void writeCoil(int offset, boolean value) throws IllegalDataAddressException {
        boolean old = getBoolean(offset, coils);
        setCoil(offset, value);
        for (ProcessImageListener l :
                writeListeners) {
            l.coilWrite(offset, old, value);
        }
    }

    @Override
    public synchronized boolean getInput(int offset) throws IllegalDataAddressException {
        return getBoolean(offset, inputs);
    }

    @Override
    public synchronized void setInput(int offset, boolean value) {
        validateOffset(offset);
        inputs.put(offset, value);
    }

    @Override
    public synchronized short getHoldingRegister(int offset) throws IllegalDataAddressException {
        return getShort(offset, holdingRegisters);
    }

    @Override
    public synchronized void setHoldingRegister(int offset, short value) {
        validateOffset(offset);
        holdingRegisters.put(offset, value);
    }

    @Override
    public synchronized void writeHoldingRegister(int offset, short value) throws IllegalDataAddressException {
        short old = getShort(offset, holdingRegisters);
        setHoldingRegister(offset, value);
        for (ProcessImageListener l :
                writeListeners) {
            l.holdingRegisterWrite(offset, old, value);
        }
    }

    @Override
    public synchronized short getInputRegister(int offset) throws IllegalDataAddressException {
        return getShort(offset, inputRegisters);
    }

    @Override
    public synchronized void setInputRegister(int offset, short value) {
        validateOffset(offset);
        inputRegisters.put(offset, value);
    }

    @Override
    public byte getExceptionStatus() {
        return exceptionStatus;
    }

    @Override
    public byte[] getReportSlaveIdData() {
        return new byte[0];
    }

    private short getShort(int offset, Map<Integer, Short> map) throws IllegalDataAddressException {
        Short value = map.get(offset);
        if (value == null) {
            if (allowInvalidAddress) {
                return invalidAddressValue;
            }
            throw new IllegalDataAddressException();
        }
        return value.shortValue();
    }

    private boolean getBoolean(int offset, Map<Integer, Boolean> map) throws IllegalDataAddressException {
        Boolean value = map.get(offset);
        if (value == null) {
            if (allowInvalidAddress) {
                return false;
            }
            throw new IllegalDataAddressException();
        }
        return value.booleanValue();
    }

    private void validateOffset(int offset) {
        if (offset < 0 || offset > 65535) {
            throw new ModbusIdException("Invalid offset: " + offset);
        }
    }

    private void validateBit(int bit) {
        if (bit < 0 || bit > 15) {
            throw new ModbusIdException("Invalid bit: " + bit);
        }
    }

    private short setBit(short s, int bit, boolean value) {
        return (short) (s | ((value ? 1 : 0) << bit));
    }

    private boolean getBit(short s, int bit) {
        return ((s >> bit) & 0x1) == 1;
    }

}
