package com.pullmi.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WareDev implements Serializable{

    public byte[] canCpuId;
    public byte[] devName;
    public byte[] roomName;
    public byte devType;
    public byte devId;
    public byte devCtrlType; // 取值范围E_DEV_TYPE
    public byte datLen;
    public byte[] dat;

    public WareDev() {}

    public WareDev(
            byte[] canCpuId, 
            byte[] devName, 
            byte[] roomName, 
            byte devType, 
            byte devId,
            byte devCtrlType, 
            byte datLen, 
            byte[] dat) {
        this.setCanCpuId(canCpuId);
        this.setDatLen(datLen);
        this.setDevCtrlType(devCtrlType);
        this.setDevId(devId);
        this.setDevName(devName);
        this.setRoomName(roomName);
        this.setType(devType);
        this.setDat(dat);
    }

    public byte[] getCanCpuId() {
        return canCpuId;
    }

    public void setCanCpuId(byte[] canCpuId) {
        this.canCpuId = canCpuId;
    }

    public byte[] getDevName() {
        return devName;
    }

    public void setDevName(byte[] devName) {
        this.devName = devName;
    }

    public byte[] getRoomName() {
        return roomName;
    }

    public void setRoomName(byte[] roomName) {
        this.roomName = roomName;
    }

    public int getType() {
        return devType;
    }

    public void setType(byte type) {
        this.devType = type;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(byte devId) {
        this.devId = devId;
    }

    public int getDevCtrlType() {
        return devCtrlType;
    }

    public void setDevCtrlType(byte devCtrlType) {
        this.devCtrlType = devCtrlType;
    }

    public int getDatLen() {
        return datLen;
    }

    public void setDatLen(byte datLen) {
        this.datLen = datLen;
    }

    public byte[] getDat() {
        return dat;
    }

    public void setDat(byte[] dat) {
        this.dat = dat;
    }
}
