package com.example.infone.utils

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper

object Config {

    private var url: String? = null
    private var apiKey: String? = null

    fun loadConfig(context: Context) {
        val fileName = "config.json"
        val fileContent = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val jsonObject = ObjectMapper().readTree(fileContent)
        this.url = jsonObject.get("url").asText()
        this.apiKey = jsonObject.get("apiKey").asText()
    }

    fun getURL(): String {
        return url ?: throw IllegalStateException("URL not set")
    }

    fun getApiKey(): String {
        return apiKey ?: throw IllegalStateException("API key not set")
    }
}
