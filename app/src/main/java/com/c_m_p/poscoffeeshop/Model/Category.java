package com.c_m_p.poscoffeeshop.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Category implements Serializable {
    private String id;
    private String name;
    private String icon;
    private Map<String, Object> items;
    private boolean isSelected;

    public Category() {} // Required for Firebase

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.icon = image;
        this.items = new HashMap<>();
        this.isSelected = false;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String image) {
        this.icon = image;
    }

    public Map<String, Object> getItems() {
        return items;
    }

    public void setItems(Map<String, Object> items) {
        this.items = items;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("name", getName());
        result.put("icon", getIcon());
        result.put("items", getItems());
        return result;
    }
}
