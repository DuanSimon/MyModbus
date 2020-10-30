package locator;

import code.DataType;
import code.RegisterRange;

import java.nio.charset.Charset;

public class StringLocator extends BaseLocator<String> {
    public static final Charset ASCII = Charset.forName("ASCII");

    private final int dataType;
    private final int registerCount;
    private final Charset charset;

    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount){
        this(slaveId, range, offset,dataType, registerCount, ASCII);
    }
    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount, Character charset){
        super(slaveId, range, offset);
        this.dataType = dataType;
        this.registerCount = registerCount;
        this.charset = charset;
        validate();
    }
    private void validate(){
        super.validate(registerCount);

        if(range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS){
            throw new IllegalDataTypeException("Only binary values can be read from Coil and Input ranges");
        }
        if(dataType != DataType.CHAR && dataType != DataType.VARCHAR){
            throw new IllegalDataTypeException("Invalid data type");
        }
    }
    @Override
    public int getDataType(){
        return dataType;
    }
    @Override
    public int getRegisterCount(){
        return registerCount;
    }
    @Override
    public String toString() {
        return "StringLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", dataType=" + dataType + ", registerCount=" + registerCount + ", charset=" + charset + ")";
    }
    @Override
    public String bytesToValueRealOffset(byte[] data, int offset){
        offset *= 2;
        int length = registerCount * 2;

        if(dataType == DataType.CHAR){
            return new String(data, offset, length, charset);
        }

        if(dataType == DataType.VARCHAR){
            int nullPos = -1;
            for (int i = offset; i < offset + length; i++) {
                if(data[i] == 0){
                    nullPos = i;
                    break;
                }
            }
            if(nullPos == -1){
                return new String(data, offset, length, charset);
            }
            return new String(data, offset, nullPos, charset);
        }
        throw new RuntimeException("Unsupported data type: " + dataType);
    }
}
