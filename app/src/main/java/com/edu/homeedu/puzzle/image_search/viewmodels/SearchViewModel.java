package com.edu.homeedu.puzzle.image_search.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.homeedu.puzzle.image_search.models.ImageResult;
import com.edu.homeedu.puzzle.image_search.utils.net.SearchClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<ImageResult>> searchResults;
    private final SearchClient searchClient;
    private static final boolean DEBUG = false; // Set to true for debugging

    public SearchViewModel(String apiKey) {
        searchResults = new MutableLiveData<>();
        searchClient = new SearchClient(apiKey);
    }

    public LiveData<List<ImageResult>> getSearchResults() {
        return searchResults;
    }

    public void searchImages(String query, int startPage) {
        searchClient.getSearch(query, startPage, response -> {
            if (response != null) {
                if (DEBUG) Log.d("SearchViewModel", "Raw response: " + response);
                try {
                    JSONArray items = response.getJSONArray("images");
                    List<ImageResult> results = ImageResult.fromJSONArray(items);
                    searchResults.postValue(results);
                    if (DEBUG) Log.d("SearchViewModel", "Fetched " + results.size() + " items.");
                } catch (Exception e) {
                    searchResults.postValue(new ArrayList<>());
                    if (DEBUG) Log.e("SearchViewModel", "Error parsing JSON", e);
                }
            } else {
                searchResults.postValue(new ArrayList<>());
                if (DEBUG) Log.e("SearchViewModel", "No response from server.");
            }
            return null;
        });
    }
}
