package com.example.foodplus;

import androidx.annotation.NonNull;

public class Ingredient {
    private String name;
    private float quantity;
    private String unit;

    public Ingredient() {
        // Default constructor required for calls to DataSnapshot.getValue(Ingredient.class)
    }

    public Ingredient(String name,float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + quantity + " " + unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
