package com.example.bakeme.service;

import com.example.bakeme.model.Recipe;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {
    @GET("android-baking-app-json")
    Single<List<Recipe>> getRecipes();
}
