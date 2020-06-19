package com.ashalab.bakingapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashalab.bakingapp.R;
import com.ashalab.bakingapp.model.Step;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepListAdapterViewHolder> {

    private Context context;
    private List<Step> stepList;
    private OnStepClickListener onStepClickListener;

    public StepListAdapter(Context context, List<Step> stepList, OnStepClickListener onStepClickListener) {
        this.context = context;
        this.stepList = stepList;
        this.onStepClickListener = onStepClickListener;
    }

    @NonNull
    @Override
    public StepListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.steps_rv_list_items, parent, false);
        return new StepListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepListAdapterViewHolder holder, int position) {
        holder.stepNumber.setText(stepList.get(position).getId().toString());
        holder.stepDescription.setText(stepList.get(position).getShortDescription());
          //  holder.photoMovie.setImage(stepList.get(position).getPhotoMovie());
      //Picasso.get().load(stepList.get(position).getPhotoMovie()).into(holder.photoMovie);
        //Picasso.with(context).load(imageUri).into(ivBasicImage);

        String photoMovie = stepList.get(position).getThumbnailURL();

        if (!photoMovie.isEmpty()) {
            Glide.with(context)
                    .load(photoMovie)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.photoMovie);
        } else {

                for (int i = 0; i < position; i++) {
                    Picasso.get().load(stepList.get(position).getThumbnailURL()).into(holder.photoMovie);


            }
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStepClickListener.onStepClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public interface OnStepClickListener {
        void onStepClicked(int position);
    }

    public class StepListAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView stepNumber;
        private TextView stepDescription;
        private ImageView photoMovie;

        public StepListAdapterViewHolder(View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_number);
            stepDescription = itemView.findViewById(R.id.short_description);
            photoMovie = itemView.findViewById(R.id.photo_movie);
        }
    }
}
