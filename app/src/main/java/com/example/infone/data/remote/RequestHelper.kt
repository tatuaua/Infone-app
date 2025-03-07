package com.example.infone.data.remote

import android.util.Log
import com.example.infone.model.DataPoint
import com.example.infone.utils.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class RequestHelper {
    suspend fun fetchDataPoints(url: String): List<DataPoint> {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .header("X-API-KEY", Config.getApiKey())
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                Log.d("RequestHelper", "DataPoints response: $responseData")

                responseData?.let {
                    return@withContext DataPoint.listFromJson(it)
                }

                emptyList()
            } catch (e: Exception) {
                Log.e("RequestHelper", "Error fetching JSON data", e)
                e.printStackTrace()
                emptyList()
            }
        }
    }
}