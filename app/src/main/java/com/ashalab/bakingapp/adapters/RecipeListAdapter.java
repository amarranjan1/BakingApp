package com.ashalab.bakingapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ashalab.bakingapp.R;
import com.ashalab.bakingapp.model.Recipe;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListAdapterViewHolder> {

    private List<Recipe> recipeList;
    private Context context;
    private RecipeItemOnClickHandler recipeItemOnClickHandler;

    public RecipeListAdapter(List<Recipe> recipeList, Context context, RecipeItemOnClickHandler recipeItemOnClickHandler) {
        this.recipeList = recipeList;
        this.context = context;
        this.recipeItemOnClickHandler = recipeItemOnClickHandler;
    }

    @NonNull
    @Override
    public RecipeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.parent_movie_list_items, parent, false);
        return new RecipeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapterViewHolder holder, int position) {
        String recipeImage = recipeList.get(position).getImage();
        String recipeName = recipeList.get(position).getName();

        holder.recipeName.setText(recipeName);

        if (!recipeImage.isEmpty()) {
            Glide.with(context)
                    .load(recipeImage)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.recipeImage);
        } else {




            switch (position) {
                case 0:
                    Picasso.get().load("https://image.tmdb.org/t/p/w600_and_h900_bestv2/c24sv2weTHPsmDa7jEMN0m2P3RT.jpg").into(holder.recipeImage);
                 //   holder.recipeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.nutella_pie));
                    break;
                case 1:
                    Picasso.get().load("https://image.tmdb.org/t/p/w600_and_h900_bestv2/8WUVHemHFH2ZIP6NWkwlHWsyrEL.jpg").into(holder.recipeImage);
                    break;
                case 2:
                    Picasso.get().load("https://image.tmdb.org/t/p/w600_and_h900_bestv2/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg").into(holder.recipeImage);
                    break;
                case 3:
                    Picasso.get().load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg").into(holder.recipeImage);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface RecipeItemOnClickHandler {
        void onClick(int position);
    }

    public class RecipeListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView recipeCardView;
        private ImageView recipeImage;
        private TextView recipeName;

        public RecipeListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeCardView = itemView.findViewById(R.id.recipe_card);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            recipeItemOnClickHandler.onClick(position);
        }
    }
}
