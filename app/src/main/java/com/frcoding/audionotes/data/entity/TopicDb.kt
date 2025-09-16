package com.frcoding.audionotes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
)
