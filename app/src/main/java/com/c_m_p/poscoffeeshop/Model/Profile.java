package com.c_m_p.poscoffeeshop.Model;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    String name;
    String phone_number;
    String address;

    public Profile() {
    }

    public Profile(String name, String phone_number, String address) {
        this.name = name;
        this.phone_number = phone_number;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("name", getName());
        result.put("phone_number", getPhone_number());
        result.put("address", getAddress());

        return result;
    }
}
