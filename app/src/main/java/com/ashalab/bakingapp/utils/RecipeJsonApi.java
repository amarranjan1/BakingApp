package com.ashalab.bakingapp.utils;


import com.ashalab.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeJsonApi {

    @GET("baking.json")
    Call<List<Recipe>> getRecipes();
}
