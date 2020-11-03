package com.simon.modbus4j.base;

public class SlaveProfile {
    private boolean writeMaskRegister = true;

    public void setWriteMaskRegister(boolean writeMaskRegister){
        this.writeMaskRegister = writeMaskRegister;
    }

    public boolean getWriteMaskRegister(){
        return writeMaskRegister;
    }
}
