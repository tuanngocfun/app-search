package com.edu.homeedu.puzzle.image_search;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

import android.util.Log;

// run debug for seeing whether it works or not
public class SerperDevTest {
    private static final String TAG = "SerperDevTest";

    @Test
    public void testSerperDevApi() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\"q\":\"apple inc\"}", mediaType);
        Request request = new Request.Builder()
                .url("https://google.serper.dev/search")
                .post(body)
                .addHeader("X-API-KEY", "da467c94e8f3d33a86ee93b48cf7d76800ab5b77")
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertNotNull("Response is null", response);
            ResponseBody responseBody = response.body();
            assertNotNull("Response body is null", responseBody);
            String responseBodyString = responseBody.string();
            //Log.d(TAG, responseBodyString);//debug
            System.out.println(responseBodyString); // logging
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Request failed: " + e.getMessage();
        }
    }
}

