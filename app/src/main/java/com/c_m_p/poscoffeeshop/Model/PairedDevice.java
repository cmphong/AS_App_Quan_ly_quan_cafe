package com.c_m_p.poscoffeeshop.Model;

public class PairedDevice {
    private String name;
    private String macAddress;
    private boolean isSelect;

    public PairedDevice(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
        isSelect = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
