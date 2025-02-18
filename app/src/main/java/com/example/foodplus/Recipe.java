package com.example.foodplus;

import java.util.List;
import java.util.Map;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients;

    public Recipe() {
    }

    public Recipe(String name, List<Ingredient> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

}
