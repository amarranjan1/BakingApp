package com.ashalab.bakingapp.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ashalab.bakingapp.model.Recipe;
import com.ashalab.bakingapp.repository.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private LiveData<List<Recipe>> recipeList;


    public RecipeViewModel() {
        recipeRepository = new RecipeRepository();
        this.recipeList = recipeRepository.getRecipeList();
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return recipeList;
    }
}
