package com.c_m_p.poscoffeeshop.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receipt {
    private String id;
    private String date_time;
    private float payment_total;
    private String table_name;
    private List<Ordered> items;

    public Receipt() {
    }

    public Receipt(String id, String date_time, float payment_total, String table_name, List<Ordered> items) {
        this.id = id;
        this.date_time = date_time;
        this.payment_total = payment_total;
        this.table_name = table_name;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public float getPayment_total() {
        return payment_total;
    }

    public void setPayment_total(float payment_total) {
        this.payment_total = payment_total;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public List<Ordered> getItems() {
        return items;
    }

    public void setItems(List<Ordered> items) {
        this.items = items;
    }

    public Map<String, Object> toMap (){
        Map<String, Object> result = new HashMap<>();
        result.put("date_time", getDate_time());
        result.put("id", getId());
        result.put("payment_total", getPayment_total());
        result.put("table_name", getTable_name());
        result.put("items", getItems());
        return result;
    }
}
