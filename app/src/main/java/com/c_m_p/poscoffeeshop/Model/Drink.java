package com.c_m_p.poscoffeeshop.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Drink implements Serializable {
    private String id;
    private String name;
    private String image;
    private String price;
    private String note;
    private String group;
    private String unit;
    private int ordered; // Popular
    private int ordering;

    public Drink() {
    }

    public Drink(
            String id, String name, String image, String price,
            String note, String group, String unit) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.note = note;
        this.group = group;
        this.unit = unit;
        this.ordered = 0;
        this.ordering = 0;
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
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }


    public void setNote(String note) {
        this.note = note;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("name", getName());
        result.put("price", getPrice());
        result.put("group", getGroup());
        result.put("image", getImage());
        result.put("unit", getUnit());
        result.put("note", getNote());
        result.put("ordered", getOrdered());
        result.put("ordering", getOrdering());

        return result;
    }

    public float getAmount(){
        return (ordering * Float.parseFloat(price));
    }
}
