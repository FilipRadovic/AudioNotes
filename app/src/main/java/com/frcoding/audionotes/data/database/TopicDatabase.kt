package com.frcoding.audionotes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.frcoding.audionotes.data.entity.TopicDb

@Database(entities = [TopicDb::class], version = 1, exportSchema = false)
abstract class TopicDatabase: RoomDatabase() {
    abstract fun getTopicDao(): TopicDao
}