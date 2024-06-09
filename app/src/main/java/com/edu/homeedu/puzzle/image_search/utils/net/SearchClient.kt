package com.edu.homeedu.puzzle.image_search.utils.net

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SearchClient(private val apiKey: String) {

    private val client = OkHttpClient()

    fun getSearch(query: String, startPage: Int, callback: (JSONObject?) -> Unit) {
        val url = "https://google.serper.dev/images"
        val mediaType = "application/json".toMediaTypeOrNull()
        val body =
            "{\"q\":\"$query\",\"location\":\"New York, New York, United States\",\"tbs\":\"qdr:m\",\"page\":$startPage}".toRequestBody(
                mediaType
            )
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SearchClient", "Request failed: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseBody = it.string()
                    Log.d("SearchClient", "Response body: $responseBody")
                    callback(JSONObject(responseBody))
                } ?: callback(null)
            }
        })
    }

}
