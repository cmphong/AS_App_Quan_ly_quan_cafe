package com.c_m_p.poscoffeeshop.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable{
    private String id;
    private String name;
    private String checkin_time;
    private String checkout_time;
    private boolean isOrdering;
    private int orderingTotal;
    private List<Ordered> items;


    public Table() {
    }

    public Table(String id, String name){
        this.id = id;
        this.name = name;
        this.checkin_time = "";
        this.checkout_time = "";
        this.isOrdering = false;
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

    public boolean getIsOrdering() {
        return isOrdering;
    }

    public void setIsOrdering(boolean isOrdering) {
        this.isOrdering = isOrdering;
    }

    public int getOrderingTotal() {
        return orderingTotal;
    }

    public void setOrderingTotal(int orderingTotal) {
        this.orderingTotal = orderingTotal;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getCheckout_time() {
        return checkout_time;
    }

    public void setCheckout_time(String checkout_time) {
        this.checkout_time = checkout_time;
    }


    public List<Ordered> getItems() {
        return items;
    }

    public void setItems(List<Ordered> items) {
        this.items = items;
    }


    public Map<String, Object> toMap (){
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("name", getName());
        result.put("isOrdering", getIsOrdering());
        result.put("orderingTotal", getOrderingTotal());
        result.put("items", getItems());
        result.put("checkin_time", getCheckin_time());
        result.put("checkout_time", getCheckout_time());
        return result;
    }
}
