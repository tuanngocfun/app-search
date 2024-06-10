package com.edu.homeedu.puzzle.image_search.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.homeedu.puzzle.image_search.R;
import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.ui.activities.ImageDisplayActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private final List<ImageResult> imageUrls;
    private final Context context;

    public ImagePagerAdapter(List<ImageResult> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageResult imageUrl = imageUrls.get(position);
        Picasso.with(context).load(imageUrl.getFullUrl()).into(holder.ivPagerImage);
        holder.tvPagerTitle.setText(imageUrl.getTitle());

        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        holder.ivPagerImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageDisplayActivity.class);
            intent.putExtra("image_list", (ArrayList<ImageResult>) imageUrls);
            intent.putExtra("start_position", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPagerImage;
        public TextView tvPagerTitle;

        public ViewHolder(View view) {
            super(view);
            ivPagerImage = view.findViewById(R.id.ivPagerImage);
            tvPagerTitle = view.findViewById(R.id.tvPagerTitle);
        }
    }
}



