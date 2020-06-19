package com.ashalab.bakingapp;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ashalab.bakingapp.databinding.RecipeDetailsActivityBinding;
import com.ashalab.bakingapp.model.Ingredient;
import com.ashalab.bakingapp.model.Recipe;
import com.ashalab.bakingapp.model.Step;

import com.ashalab.bakingapp.player.StepDetailActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


/**
 * Recipe Details activity houses two fragments: RecipeDetailFragment and StepDetailFragment
 *
 * @author mjoshi
 */
public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailFragment.PassStepClickedDataToActivity, StepDetailActivity.OnStepChangeListener {

    static String RECIPE_DETAIL = "RECIPE_DETAIL";
    static String RECIPE_STEP_DETAIL = "RECIPE_STEP_DETAIL";
    private ArrayList<Recipe> recipeArrayList;
    private int recipeCurrentPosition;
    private Recipe recipe;
    private List<Ingredient> ingredientList;
    private List<Step> stepList;
    private Bundle recipeDataBundle;
    private RecipeDetailsActivityBinding binding;
    private View view;
    private MaterialToolbar toolbar;
    private boolean mTwoPane;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeDetailsActivityBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);

        toolbar = findViewById(R.id.overhead_toolbar);
        setSupportActionBar(toolbar);


        recipeDataBundle = getIntent().getExtras();

        recipeArrayList = new ArrayList<>();
        if (recipeDataBundle != null) {
            recipeArrayList = recipeDataBundle.getParcelableArrayList(Constants.INTENT_RECIPES);
            recipeCurrentPosition = recipeDataBundle.getInt(Constants.RECIPE_POSITION);
            recipe = recipeArrayList.get(recipeCurrentPosition);

            ingredientList = recipe.getIngredients();
            stepList = recipe.getSteps();
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(recipe.getName());

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        mTwoPane = binding.recipeLinearLayout != null && binding.recipeLinearLayout.getTag().equals("tablet-land");

        if (savedInstanceState == null) {
            setupRecipeDetailFragment();
            if (mTwoPane) {
                setupStepDetailActivity(0);
            }

        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (binding.recipeLinearLayout == null) {
                    if (fragmentManager.getBackStackEntryCount() > 1) {
                        fragmentManager.popBackStack(RECIPE_DETAIL, 0);
                    } else if (fragmentManager.getBackStackEntryCount() > 0) {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    private void setupRecipeDetailFragment() {
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(recipeDataBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_detail_container, recipeDetailFragment)
                .addToBackStack(RECIPE_DETAIL)
                .commit();
    }



    private void setupStepDetailActivity(int position) {
        recipeDataBundle.putInt(Constants.STEP_POSITION, position);
        StepDetailActivity stepDetailActivity = new StepDetailActivity(stepList.get(position), position);
        //stepDetailActivity.putExtras(recipeDataBundle);

        Intent startStepDetailActivity = new Intent(this, StepDetailActivity.class);
        startStepDetailActivity.putExtras(recipeDataBundle);
        startActivity(startStepDetailActivity);

       /*
        if (mTwoPane) {
            activityManager.beginTransaction()
                    .replace(R.id.step_detail_container_tablet_land, stepDetailActivity)
                    .addToBackStack(RECIPE_STEP_DETAIL)
                    .commit();
        } else {
            activityManagerManager.beginTransaction()
                    .replace(R.id.recipe_detail_container, stepDetailActivity)
                    .addToBackStack(RECIPE_STEP_DETAIL)
                    .commit();
        }

         */
    }

    @Override
    public void passStepClickedDataToActivity(int position) {
        setupStepDetailActivity(position);
    }

    @Override
    public void onStepChangeClicked(int position) {
        passStepClickedDataToActivity(position);
    }
}
