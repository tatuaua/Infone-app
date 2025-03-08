package com.example.infone.model

import android.util.Log
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

data class DataPoint @JsonCreator constructor(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("value") val value: String,
    @JsonProperty("description") val description: String
){
    companion object {
        fun listFromJson(json: String): List<DataPoint> {
            Log.d("DataPoint", "Parsing JSON data: $json")
            val mapper = ObjectMapper();
            val dataPoints = mapper.readValue(json, Array<DataPoint>::class.java)
            return dataPoints.toList()
        }

        fun listToNotificationMessage(dataPoints: List<DataPoint>): String {
            val builder = StringBuilder()
            for (dataPoint in dataPoints) {
                builder.append("${dataPoint.name}: ${dataPoint.value}\n")
            }
            return builder.toString()
        }
    }
}