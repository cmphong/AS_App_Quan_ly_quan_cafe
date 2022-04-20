package com.c_m_p.poscoffeeshop.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Ordered implements Serializable {
    private String id;
    private String name;
    private float price;
    private int quantity;

    public Ordered() {
    }

    public Ordered(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Ordered(String id, String name, float price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotal() {
        return price * quantity;
    }
}
