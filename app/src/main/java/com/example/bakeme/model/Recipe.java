package com.example.bakeme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    // region VARIABLES
    @SerializedName("id")
    @Expose
    private Integer mId;

    @SerializedName("name")
    @Expose
    private String mName;

    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> mIngredients = null;

    @SerializedName("steps")
    @Expose
    private List<Step> mSteps = null;

    @SerializedName("servings")
    @Expose
    private Integer mServings;

    @SerializedName("image")
    @Expose
    private String mImage;
    // endregion

    // region SETTERS AND GETTERS
    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.mIngredients = ingredients;
    }

    public List<Step> getSteps() {
        return mSteps;
    }

    public void setSteps(List<Step> steps) {
        this.mSteps = steps;
    }

    public Integer getServings() {
        return mServings;
    }

    public void setServings(Integer servings) {
        this.mServings = servings;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
    // endregion
}
