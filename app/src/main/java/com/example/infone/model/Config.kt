package com.example.infone.model

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper

object Config {
    var url: String = ""

    fun loadConfig(context: Context) {
        val fileName = "config.json"
        val fileContent = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val config = ObjectMapper().readValue(fileContent, Config::class.java)
        this.url = config.url
    }
}