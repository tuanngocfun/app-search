package com.edu.homeedu.puzzle.image_search.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.edu.homeedu.puzzle.image_search.R;
import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.ui.fragments.ImageSliderFragment;
import com.edu.homeedu.puzzle.image_search.utils.BundleCompat;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageDisplayActivity extends AppCompatActivity implements ImageSliderFragment.OnImageChangeListener  {
    private static final String TAG = "ImageDisplayActivity";
    private ArrayList<ImageResult> imageResults;
    private int startPosition;
    private Button btnOpenSource;
    private static final boolean DEBUG = false; // Set to true for debugging

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        Log.d(TAG, "onCreate called");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnOpenSource = findViewById(R.id.btnOpenSource);

        Intent intent = getIntent();
        if (intent != null) {
            imageResults = (ArrayList<ImageResult>) BundleCompat.getSerializable(intent, "image_list", ArrayList.class);
            startPosition = intent.getIntExtra("start_position", 0);
            String query = intent.getStringExtra("query");
            String transitionName = intent.getStringExtra("transition_name");
            if (DEBUG) Log.d(TAG, "Received intent with start position: " + startPosition);
            if (DEBUG) Log.d(TAG, "Query: " + query);
            if (DEBUG) Log.d(TAG, "Transition name: " + transitionName);

            if (imageResults != null && DEBUG) {
                for (ImageResult result : imageResults) {
                    Log.d(TAG, "Image: " + result.getTitle() + ", Source URL: " + result.getWebsiteUrl());
                }
            }
            // Set the transition name on the target view
            ImageView imageView = findViewById(R.id.ivResult);
            if (imageView != null && transitionName != null) {
                imageView.setTransitionName(transitionName);
                Log.d(TAG, "Transition name set to: " + transitionName);
            }
        }

        if (imageResults == null || imageResults.isEmpty()) {
            Log.d(TAG, "No image results available, loading fallback view with single image");
            loadFallbackViewFromIntent();
        } else if (imageResults.size() > 1) {
            loadImageSliderFragment();
            setupSourceButton(imageResults.get(startPosition).getLink());
        } else {
            loadFallbackView(imageResults.get(0));
            setupSourceButton(imageResults.get(0).getLink());
        }

        // Handle back button for different API levels
        registerOnBackPressedCallback();
    }

    private void loadImageSliderFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            ImageSliderFragment fragment = ImageSliderFragment.newInstance(imageResults, startPosition);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            Log.d(TAG, "ImageSliderFragment loaded");

            // Use ivResult for the shared element transition setup
            ImageView imageView = findViewById(R.id.ivResult);
            setupSharedElementTransition(imageView, startPosition);
        }
    }

    private void loadImage(ImageView imageView, String url, Runnable onImageLoaded) {
        Log.d(TAG, "Loading image from URL: " + url);
        Picasso.with(this).load(url).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                if (onImageLoaded != null) {
                    onImageLoaded.run();
                }
                Log.d(TAG, "Image loaded successfully");
            }

            @Override
            public void onError() {
                if (onImageLoaded != null) {
                    onImageLoaded.run();
                }
                Log.d(TAG, "Failed to load image");
            }
        });
    }

    private void setupSharedElementTransition(ImageView imageView, int position) {
        imageView.setTransitionName("imageTransition" + position);
        postponeEnterTransition();
        startPostponedEnterTransition();
    }

    private void loadFallbackView(ImageResult imageResult) {
        ImageView ivImage = findViewById(R.id.ivResult);
        TextView tvImageName = findViewById(R.id.tvImageName);

        if (imageResult != null) {
            String url = imageResult.getFullUrl();
            setupSharedElementTransition(ivImage, startPosition);
            loadImage(ivImage, url, this::startPostponedEnterTransition);
            tvImageName.setText(imageResult.getTitle());
        } else {
            tvImageName.setText(R.string.error_no_image);
            Log.d(TAG, "No image result received in intent");
        }
    }

    @Override
    public void onImageChanged(ImageResult newImageResult) {
        setupSourceButton(newImageResult.getLink());
    }

    private void setupSourceButton(String websiteUrl) {
        btnOpenSource.setOnClickListener(v -> {
            Log.d(TAG, "Button clicked to open URL: " + websiteUrl);
            if (!URLUtil.isValidUrl(websiteUrl)) {
                Log.e(TAG, "Invalid URL: " + websiteUrl);
                Toast.makeText(this, "Invalid URL: " + websiteUrl, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                Log.d(TAG, "Starting browser intent for URL: " + websiteUrl);
                startActivity(browserIntent);
            } else {
                Log.d(TAG, "No browser found to handle the intent");
            }
        });
    }

    private void loadFallbackViewFromIntent() {
        ImageResult imageResult = getIntent().getSerializableExtra("image_list", ImageResult.class);
        loadFallbackView(imageResult);
        if (imageResult != null) {
            setupSourceButton(imageResult.getLink());
        }
    }

    private void registerOnBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackToSearchActivity();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navigateBackToSearchActivity(); // Handle the Up button action
        return true;
    }

    private void navigateBackToSearchActivity() {
        SharedPreferences preferences = getSharedPreferences("image_prefs", MODE_PRIVATE);
        int currentPosition = preferences.getInt("current_position", startPosition);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_position", currentPosition);
        editor.apply();

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("current_position", currentPosition);
        intent.putExtra("query", getIntent().getStringExtra("query"));
        startActivity(intent);
        Log.d(TAG, "Navigating back to SearchActivity with position: " + currentPosition);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        return true;
    }
}
