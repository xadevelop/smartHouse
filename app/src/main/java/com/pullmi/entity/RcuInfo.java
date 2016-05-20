package com.pullmi.entity;

public class RcuInfo {

    private byte[] devUnitID;
    private byte[] devUnitPass;
    private byte[] name;
    private static byte[] IpAddr;
    private byte[] SubMask;
    private byte[] GateWay;
    private byte[] centerServ;
    private byte[] roomNum;
    private byte[] macAddr;
    private byte[] SoftVersion;
    private byte[] HwVversion;
    private byte rev1;
    private byte rev2;
    
    public RcuInfo() {}
    
    public RcuInfo(
                   byte[] devUnitID,
                   byte[] devUnitPass,
                   byte[] name,
                   byte[] IpAddr,
                   byte[] SubMask,
                   byte[] GateWay,
                   byte[] centerServ,
                   byte[] roomNum,
                   byte[] macAddr,
                   byte[] SoftVersion,
                   byte[] HwVersion){
        
        this.setDevUnitID(devUnitID);
        this.setDevUnitPass(devUnitPass);
        this.setName(name);
        this.setIpAddr(IpAddr);
        
        
        this.setSubMask(SubMask);
        this.setGateWay(GateWay);
        this.setRoomNum(roomNum);
        this.setCenterServ(centerServ);
        this.setMacAddr(macAddr);
        this.setSoftVersion(SoftVersion);
        this.setHwVversion(HwVersion);
    }

    public byte[] getDevUnitID() {
        return devUnitID;
    }

    public void setDevUnitID(byte[] devUnitID) {
        this.devUnitID = devUnitID;
    }

    public byte[] getDevUnitPass() {
        return devUnitPass;
    }

    public void setDevUnitPass(byte[] devUnitPass) {
        this.devUnitPass = devUnitPass;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public byte[] getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(byte[] ipAddr) {
        IpAddr = ipAddr;
    }

    public byte[] getSubMask() {
        return SubMask;
    }

    public void setSubMask(byte[] subMask) {
        SubMask = subMask;
    }

    public byte[] getGateWay() {
        return GateWay;
    }

    public void setGateWay(byte[] gateWay) {
        GateWay = gateWay;
    }

    public byte[] getCenterServ() {
        return centerServ;
    }

    public void setCenterServ(byte[] centerServ) {
        this.centerServ = centerServ;
    }

    public byte[] getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(byte[] roomNum) {
        this.roomNum = roomNum;
    }

    public byte[] getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(byte[] macAddr) {
        this.macAddr = macAddr;
    }

    public byte[] getSoftVersion() {
        return SoftVersion;
    }

    public void setSoftVersion(byte[] softVersion) {
        SoftVersion = softVersion;
    }

    public byte[] getHwVversion() {
        return HwVversion;
    }

    public void setHwVversion(byte[] hwVversion) {
        HwVversion = hwVversion;
    }

}
