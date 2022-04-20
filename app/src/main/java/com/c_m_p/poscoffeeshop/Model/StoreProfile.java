package com.c_m_p.poscoffeeshop.Model;

public class StoreProfile {
    private String name;
    private String phone;
    private String logo;
    private String website;
    private String address;

    public StoreProfile(String name, String phone, String logo, String website, String address) {
        this.name = name;
        this.phone = phone;
        this.logo = logo;
        this.website = website;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
