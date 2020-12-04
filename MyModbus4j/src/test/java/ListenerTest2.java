import com.simon.modbus4j.BasicProcessImage;
import com.simon.modbus4j.ModbusFactory;
import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.code.DataType;
import com.simon.modbus4j.code.RegisterRange;
import com.simon.modbus4j.exception.ModbusInitException;

import java.util.Random;

public class ListenerTest2 {
    static Random random = new Random();
    static float ir1Value = -100;

    public static void main(String[] args) throws Exception{
        ModbusFactory modbusFactory = new ModbusFactory();
        final ModbusSlaveSet listener = modbusFactory.createTcpSlave(false);
        listener.addProcessImage(getModscanProcessImage(1));
        listener.addProcessImage(getModscanProcessImage(2));

        new Thread(new Runnable(){
            public void run(){
                try {
                    listener.start();
                }catch (ModbusInitException e){
                    e.printStackTrace();
                }
            }
        }).start();

        while(true){
            updateProcessImage1((BasicProcessImage) listener.getProcessImage(1));
            updateProcessImage2((BasicProcessImage) listener.getProcessImage(2));

            synchronized (listener){
                listener.wait(5000);
            }
        }
    }

    static void updateProcessImage1(BasicProcessImage processImage) {
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 0, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 2, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 10, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 12, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 20, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 22, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
    }

    static void updateProcessImage2(BasicProcessImage processImage) {
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 0, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 3, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 10, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 12, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 20, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 22, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 99, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 100, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
        processImage.setNumeric(RegisterRange.HOLDING_REGISTER, 101, DataType.TWO_BYTE_INT_UNSIGNED,
                random.nextInt(10000));
    }

    static BasicProcessImage getModscanProcessImage(int slaveId){
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        processImage.setAllowInvalidAddress(true);
        processImage.setInvalidAddressValue(Short.MIN_VALUE);
        processImage.setExceptionStatus((byte) 151);

        return processImage;
    }
}
