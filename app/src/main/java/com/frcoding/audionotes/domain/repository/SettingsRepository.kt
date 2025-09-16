package com.frcoding.audionotes.domain.repository

import com.frcoding.audionotes.domain.entity.MoodType

interface SettingsRepository {
    fun getMood(key: String, defaultValue: String = MoodType.Undefined.title): String

    fun saveMood(key: String, moodTitle: String)

    fun getTopics(key: String): List<Long>

    fun saveTopics(key: String, topicListId: List<Long>)
}