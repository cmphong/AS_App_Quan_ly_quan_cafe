package com.c_m_p.poscoffeeshop.Model;

public class IconObj {
    private String id;
    private int imageId;
    private String name;

    public IconObj(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public IconObj(String id, int imageId, String name) {
        this.id = id;
        this.imageId = imageId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
