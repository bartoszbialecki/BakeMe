package com.example.bakeme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient {
    // region VARIABLES
    @SerializedName("quantity")
    @Expose
    private Double mQuantity;

    @SerializedName("measure")
    @Expose
    private String mMeasure;

    @SerializedName("ingredient")
    @Expose
    private String mIngredient;
    // endregion

    // region GETTERS AND SETTERS
    public Double getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Double quantity) {
        this.mQuantity = quantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public void setMeasure(String measure) {
        this.mMeasure = measure;
    }

    public String getIngredient() {
        return mIngredient;
    }

    public void setIngredient(String ingredient) {
        this.mIngredient = ingredient;
    }
    // endregion
}
