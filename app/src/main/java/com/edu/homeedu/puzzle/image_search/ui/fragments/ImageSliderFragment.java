package com.edu.homeedu.puzzle.image_search.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.edu.homeedu.puzzle.image_search.R;
import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.ui.adapters.ImagePagerAdapter;
import com.edu.homeedu.puzzle.image_search.ui.utils.OnImageSlideListener;
import com.edu.homeedu.puzzle.image_search.utils.BundleCompat;

import java.util.ArrayList;

public class ImageSliderFragment extends Fragment {

    // These constants remain as they are used for bundle arguments
    private static final String ARG_IMAGE_LIST = "image_list";
    private static final String ARG_START_POSITION = "start_position";
    private ArrayList<ImageResult> imageList;
    private int startPosition;
    private OnImageSlideListener imageSlideListener;

    public static ImageSliderFragment newInstance(ArrayList<ImageResult> imageList, int startPosition) {
        ImageSliderFragment fragment = new ImageSliderFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE_LIST, imageList);
        args.putInt(ARG_START_POSITION, startPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_START_POSITION, startPosition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageList = BundleCompat.getSerializable(getArguments(), ARG_IMAGE_LIST, ArrayList.class);
            startPosition = getArguments().getInt(ARG_START_POSITION);
        }
        if (savedInstanceState != null) {
            startPosition = savedInstanceState.getInt(ARG_START_POSITION);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageSlideListener) {
            imageSlideListener = (OnImageSlideListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnImageSlideListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        imageSlideListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        ImagePagerAdapter adapter = new ImagePagerAdapter(imageList, getContext(), imageSlideListener);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Context context = getContext();
                if (context != null) {
                    SharedPreferences preferences = context.getSharedPreferences(getString(R.string.image_prefs), Context.MODE_PRIVATE);
                    preferences.edit().putInt(getString(R.string.current_position), position).apply();
                }
                if (imageSlideListener  != null) {
                    imageSlideListener .onImageSlide(imageList.get(position));
                }
            }
        });

        return view;
    }
}
