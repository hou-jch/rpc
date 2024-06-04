package com.hjc.example.common.model;

import java.io.Serializable;

/**
 * File Description: Color
 * Author: hou-jch
 * Date: 2024/6/3
 */
public class Color implements Serializable {
    private String color;
    private String name;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
