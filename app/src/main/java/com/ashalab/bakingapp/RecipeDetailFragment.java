package com.ashalab.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashalab.bakingapp.adapters.IngredientListAdapter;
import com.ashalab.bakingapp.adapters.StepListAdapter;

import com.ashalab.bakingapp.databinding.RecipeDetailFragmentBinding;
import com.ashalab.bakingapp.model.Ingredient;
import com.ashalab.bakingapp.model.Recipe;
import com.ashalab.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;


public class RecipeDetailFragment extends Fragment implements StepListAdapter.OnStepClickListener {

    private RecipeDetailFragmentBinding binding;

    private RecyclerView ingredientsRecyclerView;
    private IngredientListAdapter ingredientListAdapter;
    private StepListAdapter stepListAdapter;

    private RecyclerView stepsRecyclerView;

    private ArrayList<Recipe> recipeArrayList = new ArrayList<>();
    private int recipeCurrentPosition;

    private List<Ingredient> ingredientList;
    private List<Step> stepList;
    private PassStepClickedDataToActivity passStepClickedDataToActivity;

    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setFragmentDataFromSavedInstanceState(savedInstanceState);
        } else {
            setFragmentDataFromPassedBundle();
        }

        Recipe recipe = recipeArrayList.get(recipeCurrentPosition);

        ingredientList = recipe.getIngredients();
        stepList = recipe.getSteps();

        binding = RecipeDetailFragmentBinding.inflate(getLayoutInflater());
        ingredientsRecyclerView = binding.recipeIngredientsRv;
        stepsRecyclerView = binding.recipeStepsRv;
        setIngredientsView();
        setStepsView();

        StringBuilder ingredientListStringBuilder = ingredientListStringBuilder(ingredientList);

        return binding.getRoot();
    }

    private void setFragmentDataFromPassedBundle() {
        assert getArguments() != null;
        recipeArrayList = getArguments().getParcelableArrayList(Constants.INTENT_RECIPES);
        recipeCurrentPosition = getArguments().getInt(Constants.RECIPE_POSITION);
    }

    private void setFragmentDataFromSavedInstanceState(Bundle savedInstanceState) {
        recipeArrayList = savedInstanceState.getParcelableArrayList(Constants.INTENT_RECIPES);
        recipeCurrentPosition = savedInstanceState.getInt(Constants.RECIPE_POSITION);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            passStepClickedDataToActivity = (PassStepClickedDataToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PassStepClickedDataToActivity");
        }
    }

    private void setIngredientsView() {
        ingredientListAdapter = new IngredientListAdapter(ingredientList, getContext());
        ingredientsRecyclerView.setAdapter(ingredientListAdapter);
    }

    private void setStepsView() {
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        stepListAdapter = new StepListAdapter(getContext(), stepList, this);
        stepsRecyclerView.setAdapter(stepListAdapter);
    }

    @Override
    public void onStepClicked(int position) {
        passStepClickedDataToActivity.passStepClickedDataToActivity(position);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.INTENT_RECIPES, recipeArrayList);
        outState.putInt(Constants.RECIPE_POSITION, recipeCurrentPosition);
    }

    private StringBuilder ingredientListStringBuilder(List<Ingredient> ingredientList) {
        StringBuilder builder = new StringBuilder();
        for (Ingredient ingredient : ingredientList) {
            builder.append("Quantity: " + ingredient.getQuantity() + " | " + "Measure: " + ingredient.getMeasure() + " | " + "Ingredient: " + ingredient.getIngredient() + "\n");
        }
        return builder;
    }

    public interface PassStepClickedDataToActivity {
        void passStepClickedDataToActivity(int position);
    }
}
