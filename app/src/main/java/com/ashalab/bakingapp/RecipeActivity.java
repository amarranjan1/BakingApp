package com.ashalab.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashalab.bakingapp.adapters.RecipeListAdapter;

import com.ashalab.bakingapp.databinding.RecipeActivityBinding;
import com.ashalab.bakingapp.idlingresource.IdlingResourceUtils;
import com.ashalab.bakingapp.model.Recipe;

import com.ashalab.bakingapp.viewmodels.RecipeViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


/**
 * Recipe Activity which hosts the recycler view for displaying the recipe cards
 *
 * @author mjoshi
 */
public class RecipeActivity extends AppCompatActivity implements RecipeListAdapter.RecipeItemOnClickHandler {

    private RecipeViewModel recipeViewModel;

    private RecipeActivityBinding binding;
    private View view;
    private MaterialToolbar toolbar;

    private RecyclerView recipeRecyclerView;
    private RecipeListAdapter recipeListAdapter;

    private ArrayList<Recipe> recipeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeActivityBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        toolbar = findViewById(R.id.overhead_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Baking App");

        recipeRecyclerView = binding.recipeListRv;

        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        getRecipeList();
    }


    private void getRecipeList() {
        recipeViewModel.getRecipeList().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                recipeListAdapter = new RecipeListAdapter(recipes, getApplicationContext(), RecipeActivity.this);
                recipeRecyclerView.setAdapter(recipeListAdapter);
                recipeArrayList = new ArrayList<>(recipes);
                IdlingResourceUtils.setIdlingResourceState(true);
            }
        });
    }

    @Override
    public void onClick(int position) {
        Bundle recipeDataBundle = new Bundle();
        recipeDataBundle.putParcelableArrayList(Constants.INTENT_RECIPES, this.recipeArrayList);
        recipeDataBundle.putInt(Constants.RECIPE_POSITION, position);

        Intent startRecipeDetailsActivity = new Intent(this, RecipeDetailsActivity.class);
        startRecipeDetailsActivity.putExtras(recipeDataBundle);
        startActivity(startRecipeDetailsActivity);
    }
}
