package locator;

import code.DataType;
import code.RegisterRange;
import org.omg.CORBA.DATA_CONVERSION;
import sun.security.util.ArrayUtil;

import java.math.RoundingMode;

public class NumericLocator extends BaseLocator<Number> {
    private static final int[] DATA_TYPE = {
            DataType.TWO_BYTE_INT_UNSIGNED,
            DataType.TWO_BYTE_INT_SIGNED,
            DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED,
            DataType.TWO_BYTE_INT_SIGNED_SWAPPED,
            DataType.FOUR_BYTE_INT_UNSIGNED,
            DataType.FOUR_BYTE_INT_SIGNED,
            DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
            DataType.FOUR_BYTE_INT_SIGNED_SWAPPED,
            DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED,
            DataType.FOUR_BYTE_INT_SIGNED_SWAPPED_SWAPPED,
            DataType.FOUR_BYTE_FLOAT,
            DataType.FOUR_BYTE_FLOAT_SWAPPED,
            DataType.EIGHT_BYTE_INT_UNSIGNED,
            DataType.EIGHT_BYTE_INT_SIGNED,
            DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED,
            DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED,
            DataType.EIGHT_BYTE_FLOAT,
            DataType.EIGHT_BYTE_FLOAT_SWAPPED,
            DataType.TWO_BYTE_BCD,
            DataType.FOUR_BYTE_BCD,
            DataType.FOUR_BYTE_BCD_SWAPPED,
            DataType.FOUR_BYTE_MOD_10K,
            DataType.FOUR_BYTE_MOD_10K_SWAPPED,
            DataType.SIX_BYTE_MOD_10K,
            DataType.SIX_BYTE_MOD_10K_SWAPPED,
            DataType.EIGHT_BYTE_MOD_10K,
            DataType.EIGHT_BYTE_MOD_10K_SWAPPED,
            DataType.ONE_BYTE_INT_UNSIGNED_LOWER,
            DataType.ONE_BYTE_INT_UNSIGNED_UPPER
    };

    private final int dataType;
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    public NumericLocator(int slaveId, int range, int offset, int dataType){
        super(slaveId, range, offset);
        this.dataType = dataType;
        validate();
    }
    private void validate(){
        super.validate(getRegisterCount());
        if(range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS){
            throw new IllegalDataTypeException("Only binary values can be read from Coil and Input ranges");
        }
        if(!ArrayUtil.contains(DATA_TYPES, dataType)){
            throw new IllegalDataTypeException("Invalid data type");
        }
    }
    public int getDataType(){
        return dataType;
    }
    public RoundingMode getRoundingMode(){
        return roundingMode;
    }
    public void setRoundingMode(RoundingMode roundingMode){
        this.roundingMode = roundingMode;
    }
    @Override
    public String toString(){
        return "NumericLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", dataType=" + dataType + ")";
    }
    @Override
    public int getRegisterCount(){
        switch (dataType){
            case DataType.TWO_BYTE_INT_UNSIGNED:
            case DataType.TWO_BYTE_INT_SIGNED:
            case DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED:
            case DataType.TWO_BYTE_INT_SIGNED_SWAPPED:
            case DataType.TWO_BYTE_BCD:
            case DataType.ONE_BYTE_INT_UNSIGNED_LOWER:
            case DataType.ONE_BYTE_INT_UNSIGNED_UPPER:
                return 1;
            case DataType.FOUR_BYTE_INT_UNSIGNED:
            case DataType.FOUR_BYTE_INT_SIGNED:
            case DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED:
            case DataType.FOUR_BYTE_INT_SIGNED_SWAPPED:
            case DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED_SWAPPED:
            case DataType.FOUR_BYTE_FLOAT:
            case DataType.FOUR_BYTE_FLOAT_SWAPPED:
            case DataType.FOUR_BYTE_BCD:
            case DataType.FOUR_BYTE_BCD_SWAPPED:
            case DataType.FOUR_BYTE_MOD_10K:
            case DataType.FOUR_BYTE_MOD_10K_SWAPPED:
                return 2;
            case DataType.SIX_BYTE_MOD_10K:
            case DataType.SIX_BYTE_MOD_10K_SWAPPED:
                return 3;
            case DataType.EIGHT_BYTE_INT_UNSIGNED:
            case DataType.EIGHT_BYTE_INT_SIGNED:
            case DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED:
            case DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED:
            case DataType.EIGHT_BYTE_FLOAT:
            case DataType.EIGHT_BYTE_FLOAT_SWAPPED:
            case DataType.EIGHT_BYTE_MOD_10K:
            case DataType.EIGHT_BYTE_MOD_10K_SWAPPED:
                return 4;
        }
        throw new RuntimeException("Unsupported data type: " + dataType);
    }
    @Override
    public Number bytesToValueRealOffset(byte[] data, int offset){
        offset *= 2;

        if(dataType == DataType.TWO_BYTE_INT_UNSIGNED){
            return new Integer(((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
        }
        if(dataType == DataType.TWO_BYTE_INT_SIGNED){
            return new Short((short)(((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff)));
        }
        if(dataType == DataType.TWO_BYTE_INT_UNSIGNED_SWAPPED){
            return new Integer(((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff));
        }
        if(dataType == DataType.TWO_BYTE_INT_SIGNED_SWAPPED){
            return new Short((short)(((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff)));
        }
        if(dataType == DataType.TWO_BYTE_BCD){
            StringBuilder sb = new StringBuilder();
            appendBCD(sb, data[offset]);
            appendBCD(sb, data[offset + 1]);
            return Short.parseShort(sb.toString());
        }
        if()
    }
}
