package com.c_m_p.poscoffeeshop.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Category_sub {
    private String id;
    private String name;
    private int icon;
    private Map<String, Object> items;
    private String parent;


    public Category_sub() { // Required for Firebase
    }

    public Category_sub(String id, String name, int image, String parent) {
        this.id = id;
        this.name = name;
        this.icon = image;
        this.parent = parent;
        this.items = new HashMap<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, Object> getItems() {
        return items;
    }

    public void setItems(Map<String, Object> items) {
        this.items = items;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("name", getName());
        result.put("icon", getIcon());
        result.put("parent", getParent());
        result.put("items", getItems());
        return result;
    }
}
