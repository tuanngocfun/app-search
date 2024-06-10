package com.edu.homeedu.puzzle.image_search.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.edu.homeedu.puzzle.image_search.models.ImageResult;

import java.util.List;

public class ImageDiffCallback extends DiffUtil.Callback {

    private final List<ImageResult> oldList;
    private final List<ImageResult> newList;

    public ImageDiffCallback(List<ImageResult> oldList, List<ImageResult> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getFullUrl().equals(newList.get(newItemPosition).getFullUrl());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}