package com.pullmi.entity;

import java.io.Serializable;

public class WareValve implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public WareDev dev;
    public byte bOnOff;
    public byte timRun;
    public byte powChnOpen;
    public byte powChnClose;
}
