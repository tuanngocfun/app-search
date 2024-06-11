package com.edu.homeedu.puzzle.image_search.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.edu.homeedu.puzzle.image_search.R;
import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.utils.ImageDiffCallback;

import java.util.List;

public class ImageResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private final List<ImageResult> imageResults;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = "ImageResultAdapter";

    public ImageResultAdapter(List<ImageResult> imageResults) {
        this.imageResults = imageResults;
    }

    public interface OnItemClickListener {
        void onItemClick(ImageResult imageResult, int position, ImageView imageView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_result, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder itemViewHolder) {
            ImageResult imageResult = imageResults.get(position);
            Glide.with(holder.itemView.getContext())
                    .load(imageResult.getThumbUrl())
                    .into(itemViewHolder.imageView);
            itemViewHolder.textView.setText(imageResult.getTitle());

            String transitionName = holder.itemView.getContext().getString(R.string.imageTransition) + position;
            itemViewHolder.imageView.setTransitionName(transitionName);

            itemViewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(imageResult, position, itemViewHolder.imageView);
                }
            });
        } else {
            Log.d(TAG, "onBindViewHolder: Loading view");
        }

    }

    @Override
    public int getItemViewType(int position) {
        return imageResults.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return imageResults == null ? 0 : imageResults.size();
    }

    public void updateResults(List<ImageResult> newResults) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ImageDiffCallback(this.imageResults, newResults));
        imageResults.clear();
        imageResults.addAll(newResults);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addResults(List<ImageResult> newResults) {
        int startPosition = imageResults.size();
        imageResults.addAll(newResults);
        notifyItemRangeInserted(startPosition, newResults.size());
    }

    public void addLoading() {
        if (imageResults != null && !imageResults.isEmpty() && imageResults.get(imageResults.size() - 1) != null) {
            imageResults.add(null);
            notifyItemInserted(imageResults.size() - 1);
        }
    }

    public void removeLoading() {
        if (imageResults != null && !imageResults.isEmpty()) {
            int position = imageResults.size() - 1;
            ImageResult item = imageResults.get(position);
            if (item == null) {
                imageResults.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
            textView = itemView.findViewById(R.id.tvTitle);
        }
    }
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
