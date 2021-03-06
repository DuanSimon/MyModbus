package com.simon.modbus4j.code;

public class RegisterRange {
    public static final int COIL_STATUS = 1;
    public static final int INPUT_STATUS = 2;
    public static final int HOLDING_REGISTER = 3;
    public static final int INPUT_REGISTER = 4;

    public static int getFrom(int id) {
        switch (id) {
            case COIL_STATUS:
                return 0;
            case INPUT_STATUS:
                return 0x10000;
            case HOLDING_REGISTER:
                return 0x40000;
            case INPUT_REGISTER:
                return 0x30000;
        }
        return -1;
    }

    public static int getTo(int id) {
        switch (id) {
            case COIL_STATUS:
                return 0xffff;
            case INPUT_STATUS:
                return 0x1ffff;
            case HOLDING_REGISTER:
                return 0x4ffff;
            case INPUT_REGISTER:
                return 0x3ffff;
        }
        return -1;
    }

    public static int getReadFunctionCode(int id) {
        switch (id) {
            case COIL_STATUS:
                return FunctionCode.READ_COILS;
            case INPUT_STATUS:
                return FunctionCode.READ_DISCRETE_INPUTS;
            case HOLDING_REGISTER:
                return FunctionCode.READ_HOLDING_REGISTERS;
            case INPUT_REGISTER:
                return FunctionCode.READ_INPUT_REGISTERS;
        }
        return -1;
    }
}
