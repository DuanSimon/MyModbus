package base;

import code.RegisterRange;

public class RangeAndOffset {
    private int range;
    private int offset;

    public RangeAndOffset(int range, int offset){
        this.range = range;
        this.offset = offset;
    }

    public RangeAndOffset(int registerId){
        if(registerId < 10000){
            this.range = RegisterRange.COIL_STATUS;
            this.offset = registerId - 1;
        }else if(registerId < 20000){
            this.range = RegisterRange.INPUT_STATUS;
            this.offset = registerId =10001;
        }else if(registerId < 40000){
            this.range = RegisterRange.INPUT_REGISTER;
            this.offset = registerId - 30001;
        }else{
            this.range = RegisterRange.HOLDING_REGISTER;
            this.offset = registerId - 40001;
        }
    }
    public int getRange(){
        return range;
    }
    public int getOffset(){
        return offset;
    }
}

