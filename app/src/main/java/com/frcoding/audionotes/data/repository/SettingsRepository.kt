package com.frcoding.audionotes.data.repository

import android.content.SharedPreferences
import com.frcoding.audionotes.domain.repository.SettingsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
    private val gson = Gson()

    override fun getMood(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun saveMood(key: String, moodTitle: String) {
        sharedPreferences.edit()
            .putString(key, moodTitle)
            .apply()
    }

    override fun getTopics(key: String): List<Long> {
        val json = sharedPreferences.getString(key, null)
        if (json != null) {
            val type = object : TypeToken<List<Long>>() {}.type
            return gson.fromJson(json, type)
        }
        return emptyList()
    }

    override fun saveTopics(key: String, topicListId: List<Long>) {
        val json = gson.toJson(topicListId)
        sharedPreferences.edit()
            .putString(key, json)
            .apply()
    }
}