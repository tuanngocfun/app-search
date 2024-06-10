package com.edu.homeedu.puzzle.image_search.ui.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.homeedu.puzzle.image_search.R;
import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.ui.adapters.ImageResultAdapter;
import com.edu.homeedu.puzzle.image_search.utils.helpers.EndlessScrollListener;
import com.edu.homeedu.puzzle.image_search.viewmodels.SearchViewModel;
import com.edu.homeedu.puzzle.image_search.viewmodels.SearchViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity  {
    private static final String TAG = "SearchActivity";
    private static final int MAX_PAGE = 10;
    private EditText etQuery;
    private ImageResultAdapter adapter;
    private SearchViewModel viewModel;

    private ExecutorService executorService;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private final ArrayList<ImageResult> imageResults = new ArrayList<>();
    private  RecyclerView rvResults;
    private ProgressBar progressBar;
    private int currentPage = 1;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d(TAG, "onCreate called");

        executorService = Executors.newSingleThreadExecutor();
        searchHandler = new Handler(Looper.getMainLooper());

        setupViews();

        String apiKey = "da467c94e8f3d33a86ee93b48cf7d76800ab5b77";
        SearchViewModelFactory factory = new SearchViewModelFactory(apiKey);
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);
        viewModel.getSearchResults().observe(this, this::onSearchResults);

        preferences = getSharedPreferences("image_prefs", MODE_PRIVATE);
        String lastQuery = preferences.getString("last_query", "");
        String imageResultsJson = preferences.getString("image_results", "");

        if (!lastQuery.isEmpty() && !imageResultsJson.isEmpty()) {
            etQuery.setText(lastQuery);
            imageResults.clear();
            imageResults.addAll(new Gson().fromJson(imageResultsJson, new TypeToken<List<ImageResult>>(){}.getType()));
            adapter.updateResults(imageResults);
        }

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("page");
            rvResults.scrollToPosition(savedInstanceState.getInt("position"));
        }
    }

    private void setupViews() {
        etQuery = findViewById(R.id.etQuery);
        rvResults = findViewById(R.id.rvResults);
        progressBar = findViewById(R.id.progressBar);
        Button btnSearch = findViewById(R.id.btnSearch);

        progressBar.setVisibility(View.GONE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvResults.setLayoutManager(gridLayoutManager);
        adapter = new ImageResultAdapter(new ArrayList<>());
        rvResults.setAdapter(adapter);

        adapter.setOnItemClickListener(this::onImageItemClick);

        etQuery.setOnClickListener(v -> {
            etQuery.requestFocus();
            showSoftKeyboard(etQuery);
        });

        etQuery.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "onEditorAction: actionId=" + actionId);
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard(v);
                debounceSearch(1);
                return true;
            }
            return false;
        });

        btnSearch.setOnClickListener(v -> {
            hideSoftKeyboard(v);
            debounceSearch(1);
        });

        rvResults.addOnScrollListener(new EndlessScrollListener(gridLayoutManager) {
            @Override
            protected void onLoadMore(int page, int totalItemsCount) {
                if (page <= MAX_PAGE) {
                    debounceSearch((10 * (page - 1)) + 1);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rvResults.getLayoutManager() != null) {
            outState.putString("query", etQuery.getText().toString());
            outState.putInt("page", currentPage);
            outState.putInt("position", ((GridLayoutManager) rvResults.getLayoutManager()).findFirstVisibleItemPosition());
            Log.d(TAG, "Saving instance state: query=" + etQuery.getText().toString());
        } else {
            Log.e(TAG, "RecyclerView LayoutManager is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("image_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_query", etQuery.getText().toString());
        editor.putString("image_results", new Gson().toJson(imageResults));
        editor.apply();
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void onSearchResults(List<ImageResult> results) {
        progressBar.setVisibility(View.GONE);// Hide progress bar once results are received
        adapter.removeLoading();

        if (results != null && !results.isEmpty()) {
            imageResults.addAll(results);
            adapter.addResults(results);
            cacheResults(); // Cache the updated results
            Log.d(TAG, "Results updated with " + results.size() + " items.");
        } else if (adapter.getItemCount() == 0) { // Only show message if adapter is empty
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No results found.");
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void debounceSearch(int start) {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        searchRunnable = () -> onImageSearch(start);
        searchHandler.postDelayed(searchRunnable, 500);  // 500ms delay to ensure proper debounce
        Log.d(TAG, "debounceSearch called with start: " + start);
    }

    private void onImageSearch(int start) {
        Log.d(TAG, "Starting search with query: " + etQuery.getText().toString() + " from start: " + start);
        if (isNetworkAvailable()) {
            String query = etQuery.getText().toString();
            if (start == 1) {
                imageResults.clear(); // Clear previous results
                adapter.updateResults(new ArrayList<>());
                currentPage = 1;
                progressBar.setVisibility(View.VISIBLE); // Show progress bar on new search
            } else {
                adapter.addLoading();
                progressBar.setVisibility(View.VISIBLE); // Show progress bar on load more
            }

            if (!query.isEmpty()) {
                executorService.submit(() -> {
                    Log.d(TAG, "Executing search in background for query: " + query);
                    viewModel.searchImages(query, start);
                    prefetchNextPage(query, start + 10);
                });
            } else {
                Toast.makeText(this, R.string.invalid_query, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Invalid query.");
                progressBar.setVisibility(View.GONE);// Hide if query is invalid
            }
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No internet connection.");
            progressBar.setVisibility(View.GONE); // Hide if no internet connection
        }
    }

    private void prefetchNextPage(String query, int nextPage) {
        if (nextPage <= MAX_PAGE * 10) { //within max range
            Log.d(TAG, "Prefetching next page: " + nextPage);
            viewModel.searchImages(query, nextPage);
        }
    }

    private void cacheResults() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("image_results", new Gson().toJson(imageResults));
        editor.apply();
    }

    private ArrayList<ImageResult> getImageResults() {
        return imageResults;
    }

    private void onImageItemClick(ImageResult imageResult, int position, View sharedImageView) {
        if (imageResult != null) {
            Intent intent = new Intent(this, ImageDisplayActivity.class);
            intent.putExtra("image_list", getImageResults());
            intent.putExtra("start_position", position);
            intent.putExtra("query", etQuery.getText().toString());
            intent.putExtra("transition_name", sharedImageView.getTransitionName());

            Log.d(TAG, "Starting shared element transition with name: " + sharedImageView.getTransitionName());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    sharedImageView,
                    sharedImageView.getTransitionName()
            );

            startActivity(intent, options.toBundle());
            Log.d(TAG, "Image clicked: " + imageResult.getTitle() + " at position: " + position);
        } else {
            Log.e("SearchActivity", "ImageResult is null");
        }
    }
}
