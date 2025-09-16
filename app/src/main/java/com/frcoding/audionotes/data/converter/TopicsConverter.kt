package com.frcoding.audionotes.data.converter

import androidx.room.TypeConverter

class TopicsConverter {
    @TypeConverter
    fun fromTopicsList(topics: List<String>): String {
        return topics.joinToString(",")
    }

    @TypeConverter
    fun toTopicsList(value: String): List<String> {
        return value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}