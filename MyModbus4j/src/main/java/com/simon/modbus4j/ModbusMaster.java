package com.simon.modbus4j;

import com.simon.modbus4j.base.ReadFunctionGroup;
import com.simon.modbus4j.base.SlaveProfile;
import com.simon.modbus4j.code.DataType;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.code.RegisterRange;
//import com.oracle.jrockit.jfr.DataType;
import com.simon.modbus4j.exception.ErrorResponseException;
import com.simon.modbus4j.exception.InvalidDataConversionException;
import com.simon.modbus4j.locator.BinaryLocator;
import com.simon.modbus4j.msg.*;
import com.simon.modbus4j.sero.epoll.InputStreamEPollWrapper;
import com.simon.modbus4j.sero.log.BaseIOLog;
import com.sun.javaws.exceptions.ErrorCodeResponseException;
import com.simon.modbus4j.exception.ModbusInitException;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.locator.BaseLocator;
import com.simon.modbus4j.sero.messaging.MessageControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class ModbusMaster extends Modbus {

    private int timeout = 500;
    private int retries = 2;
    protected boolean validateResponse;
    protected boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    private boolean multipleWritesOnly;
    private int discardDataDelay = 0;
    private BaseIOLog ioLog;
    private InputStreamEPollWrapper ePoll;
    private final Map<Integer, SlaveProfile> slaveProfiles = new HashMap<>();
    protected boolean initialized;

    abstract public void init() throws ModbusInitException;

    public boolean isInitialized() {
        return initialized;
    }

    abstract public void destroy();

    public final ModbusResponse send(ModbusRequest request) throws ModbusTransportException {
        request.validate(this);
        ModbusResponse modbusResponse = sendImpl(request);
        if (validateResponse) {
            modbusResponse.validateResponse(request);
        }
        return modbusResponse;
    }

    abstract public ModbusResponse sendImpl(ModbusRequest request) throws ModbusTransportException;

    public <T> T getValue(BaseLocator<T> locator) throws ModbusTransportException, ErrorResponseException {
        BatchRead<String> batch = new BatchRead<>();
        batch.addLocator("", locator);
        BatchResults<String> result = send(batch);
        return (T) result.getValue("");
    }

    public <T> void setValue(BaseLocator<T> locator, Object value) throws ModbusTransportException, ErrorCodeResponseException {
        int slaveId = locator.getSlaveId();
        int registerRange = locator.getRange();
        int writeOffset = locator.getOffset();

        if (registerRange == RegisterRange.INPUT_STATUS || registerRange == RegisterRange.INPUT_REGISTER) {
            throw new RuntimeException("Can not write to input status or input register ranges");
        }
        if (registerRange == RegisterRange.COIL_STATUS) {
            if (!(value instanceof Boolean)) {
                throw new InvalidDataConversionException("Only boolean values can be writen to coils");
            }
            if (multipleWritesOnly) {
                setValue(new WriteCoilsRequest(slaveId, writeOffset, new boolean[]{((boolean) ((Boolean) value).booleanValue())}));
            } else {
                setValue(new WriteCoilRequest(slaveId, writeOffset, ((Boolean) value).booleanValue()));
            }
        } else {
            if (locator.getDataType() == DataType.BINARY) {
                if (!(value instanceof Boolean)) {
                    throw new InvalidDataConversionException("Only boolean values can be written to coils");
                }
                setHoldingRegisterBit(slaveId, writeOffset, ((BinaryLocator) locator).getBit(), ((Boolean) value).booleanValue());
            } else {
                short[] data = locator.valueToShort((T) value);
                if (data.length == 1 && !multipleWritesOnly) {
                    setValue(new WriteRegisterRequest(slaveId, writeOffset, data[0]));
                } else {
                    setValue(new WriteRegistersRequest(slaveId, writeOffset, data));
                }
            }
        }
    }

    public List<Integer> scanForSlaveNodes() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 1; i < 240; i++) {
            if (testSlaveNode(i)) {
                result.add(i);
            }
        }
        return result;
    }

    public ProgressiveTask scanForSlaveNodes(final NodeScanListener l) {
        l.progressUpdate(0);
        ProgressiveTask task = new ProgressiveTask(l) {
            private int node = 1;

            @Override
            protected void runImpl() {
                if (testSlaveNode(node)) {
                    l.nodeFound(node);
                }
                declareProgress(((float) node) / 240);
                node++;
                if (node > 240) {
                    completed = true;
                }
            }
        };
        new Thread(task).start();
        return task;
    }

    public boolean testSlaveNode(int node) {
        try {
            send(new ReadHoldingRegistersRequest(node, 0, 1));
        } catch (ModbusTransportException e) {
            return false;
        }
        return true;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        if (retries < 0) {
            this.retries = 0;
        } else {
            this.retries = retries;
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        if (timeout < 1) {
            this.timeout = 1;
        } else {
            this.timeout = timeout;
        }
    }

    public boolean isMultipleWritesOnly() {
        return multipleWritesOnly;
    }

    public void setMultipleWritesOnly(boolean multipleWritesOnly) {
        this.multipleWritesOnly = multipleWritesOnly;
    }

    public int getDiscardDataDelay() {
        return discardDataDelay;
    }

    public void setDiscardDataDelay(int discardDataDelay) {
        if (discardDataDelay < 0) {
            this.discardDataDelay = 0;
        } else {
            this.discardDataDelay = discardDataDelay;
        }
    }

    public BaseIOLog getIoLog() {
        return ioLog;
    }

    public void setIoLog(BaseIOLog ioLog) {
        this.ioLog = ioLog;
    }

    public InputStreamEPollWrapper getePoll() {
        return ePoll;
    }

    public void setePoll(INputStreamEPollWrapper ePoll) {
        this.ePoll = ePoll;
    }

    public <K> BatchResults<K> send(BatchRead<K> batch) throws ModbusTransportException, ErrorResponseException {
        if (!initialized) {
            throw new ModbusTransportException("not initialized");
        }
        BatchResults<K> results = new BatchResults<>();
        List<ReadFunctionGroup<K>> functionGroups = batch.getReadFunctionGroups(this);
        for (ReadFunctionGroup<K> functionGroup :
                functionGroups) {
            sendFunctionGroup(functionGroup, results, batch.isErrorsInResults(), batch.isExceptionsInResults());
            if (batch.isCancel()) {
                break;
            }
        }
        return results;
    }

    protected MessageControl getMessageControl() {
        MessageControl conn = new MessageControl();
        conn.setRetries(getRetries());
        conn.setTimeout(getTimeout());
        conn.setDiscardDataDelay(getDiscardDataDelay());
        conn.setExceptionHandler(getExceptionHandler());
        conn.setIoLog(ioLog);
        return conn;
    }

    protected void closeMesdageControl(MessageControl conn) {
        if (conn != null) {
            conn.close();
        }
    }

    private <K> void sendFunctionGroup(ReadFunctionGroup<K> functionGroup, BatchResults<K> results,
                                       boolean errorsInResults, boolean exceptionsInResults)
            throws ModbusTransportException, ErrorResponseException {
        int slaveId = functionGroup.getSlaveAndRange().getSlaveId();
        int startOffset = functionGroup.getStartOffset();
        int length = functionGroup.getLength();

        ModbusRequest request;
        if (functionGroup.getFunctionCode() == FunctionCode.READ_COILS) {
            request = new ReadCoilsRequest(slaveId, startOffset, length);
        } else if (functionGroup.getFunctionCode() == FunctionCode.READ_DISCRETE_INPUTS) {
            request = new ReadDiscreteInputsRequest(slaveId, startOffset, length);
        } else if (functionGroup.getFunctionCode() == FunctionCode.READ_HOLDING_REGISTERS) {
            request = new ReadHoldingReqistersRequest(slaveId, startOffset, length);
        } else if (functionGroup.getFunctionCode == FunctionCode.READ_INPUT_REGISTERS) {
            request = new ReadInputRegistion(slaveId, startOffset, length);
        } else {
            throw new RuntimeException("Unsupported function");
        }
        ReadResponse response;
        try {
            response = (ReadResponse) send(request);
        } catch (ModbusTransportException e) {
            if (!exceptionsInResults) {
                throw e;
            }
            for (KeyedModbusLocator<K> locator :
                    functionGroup.getLocators()) {
                results.addResult(locator.getKey(), e);
            }
            return;
        }

        byte[] data = null;
        if (!errorsInResults && response.isException()) {
            results.addResult(locator.getKey(), new ExceptionResult(reponse.getExcepionCode()));
        } else {
            try {
                results.addResult(locator.getKey(), locator.bytesToValue(data, startOffset));
            } catch (RuntimeException e) {
                throw new RuntimeException("Result conversion exception. data = " + ArrayUtils.toHexString(data)
                        + ", startOffset = " + startOffset
                        + ", locator = " + locator
                        + ", functionGroup.functionCode = " + fucntionGroup.getFunctionCode()
                        + ", functionGroup.startOffset = " + startOffset
                        + ", functionGroup.length = " + length, e
                );
            }
        }
    }

    private void setValue(ModbusRequest request) throws ModbusTransportException, ErrorResponseException {
        ModbusResponse response = send(request);
        if (response == null) {
            return;
        }
        if (response.isException()) {
            throw new ErrorResponseException(request, response);
        }
    }

    private void setHoldingRegisterBit(int slaveId, int writeOffset, int bit, boolean value)
            throws ModbusTransportException, ErrorCodeResponseException {
        SlaveProfile sp = getSlaveProfile(slaveId);
        if (sp.getWriteMaskRegister()) {
            WriteMaskRegisterRequest request = new WriteMaskRegisterRequest(slaveId, writeOffset);
            request.setBit(bit, value);
            ModbusResponse response = send(request);
            if (response == null) {
                return;
            }
            if (!response.isException()) {
                return;
            }
            if (response.getExceptionCode() == ExceptionCode.ILLEGAL_FUNCTION) {
                sp.setWriteMaskRegister(false);
            } else {
                throw new ErrorCodeResponseException(request, response);
            }

        }
        int regValue = (Integer) getValue(new NumericLocator(slaveId, RegisterRange.HOLDING_REGISTER, writeOffset, DataType.TWO_BYTE_INT_UNSIGNED));
        if (value) {
            regValue = regValue | 1 << bit;
        } else {
            regValue = regValue & ~(1 << bit);
        }
        setValue(new WriteRegisterRequest(slaveId, writeOffset, regValue));
    }

    private SlaveProfile getSlaveProfile(int slaveId) {
        SlaveProfile sp = slaveProfiles.get(slaveId);
        if (sp == null) {
            sp = new SlaveProfile();
            slaveProfiles.put(slaveId, sp);
        }
        return sp;
    }
}
