package com.c_m_p.poscoffeeshop.Model;

import com.emh.thermalprinter.connection.bluetooth.BluetoothConnection;

public class MyThermalPrinter {
    private String id;
    private String name;
    private String ipAddress;
    private int portAddress;
    private String macAddress;
    private String paperSize;
    private String method;

//    private BluetoothConnection selectedBluetoothDevice;

    public MyThermalPrinter(String id, String name, String ipAddress, String macAddress,
                            String paperSize, String method){
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.portAddress = 9100;
        this.macAddress = macAddress;
        this.paperSize = paperSize;
        this.method = method;
//        this.selectedBluetoothDevice = selectedBluetoothDevice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortAddress() {
        return portAddress;
    }

    public void setPortAddress(int portAddress) {
        this.portAddress = portAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

//    public BluetoothConnection getSelectedBluetoothDevice() {
//        return selectedBluetoothDevice;
//    }
//
//    public void setSelectedBluetoothDevice(BluetoothConnection selectedBluetoothDevice) {
//        this.selectedBluetoothDevice = selectedBluetoothDevice;
//    }
}
