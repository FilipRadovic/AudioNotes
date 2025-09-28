package com.frcoding.audionotes.data.converter

import androidx.room.TypeConverter
import com.frcoding.audionotes.domain.entity.MoodType

class MoodTypeConverter {
    @TypeConverter
    fun fromMoodType(moodType: MoodType): String = moodType.title

    @TypeConverter
    fun toMoodType(value: String): MoodType {
        return when (value) {
            MoodType.Excited.title -> MoodType.Excited
            MoodType.Neutral.title -> MoodType.Neutral
            MoodType.Peaceful.title -> MoodType.Peaceful
            MoodType.Sad.title -> MoodType.Sad
            MoodType.Stressed.title -> MoodType.Stressed
            else -> MoodType.Undefined
        }
    }
}