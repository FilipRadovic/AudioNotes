package com.frcoding.audionotes.data.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.frcoding.audionotes.data.entity.TopicDb
import kotlinx.coroutines.flow.Flow

interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getTopics(): Flow<List<TopicDb>>

    @Query("SELECT * FROM topics WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchTopics(query: String): List<TopicDb>

    @Insert
    suspend fun insertTopic(topicDb: TopicDb)

    @Delete
    suspend fun deleteTopic(topicDb: TopicDb)

    @Query("SELECT * FROM topics WHERE id IN (:topicIdList)")
    suspend fun getTopicsByIdList(topicIdList: List<Long>): List<TopicDb>
}