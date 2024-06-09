package com.edu.homeedu.puzzle.image_search.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ImageResult implements Serializable {
    private String fullUrl;
    private String link;
    private String thumbUrl;
    private String title;
    private String websiteUrl;

    public String getFullUrl() {
        return fullUrl;
    }
    public String getLink() {
        return link;
    }
    public String getThumbUrl() {
        return thumbUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public ImageResult(JSONObject json){
        try {
            this.fullUrl = json.getString("imageUrl");
            this.title = json.getString("title");
            this.link = json.getString("link");
            this.thumbUrl = json.getString("thumbnailUrl");
            this.websiteUrl = json.getString("domain");
        }catch (JSONException e){
            Log.e("SearchActivity", "Invalid JSON data", e);
        }
    }

    public static ArrayList<ImageResult> fromJSONArray (JSONArray array){
        ArrayList<ImageResult> results = new ArrayList<>();
        for (int i=0; i < array.length(); i++){
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            }catch (JSONException e){
                Log.e("SearchActivity", "Invalid JSON data", e);
            }
        }
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageResult that = (ImageResult) o;
        return Objects.equals(fullUrl, that.fullUrl) &&
                Objects.equals(link, that.link) &&
                Objects.equals(thumbUrl, that.thumbUrl) &&
                Objects.equals(title, that.title) &&
                Objects.equals(websiteUrl, that.websiteUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullUrl, link, thumbUrl, title, websiteUrl);
    }
}
