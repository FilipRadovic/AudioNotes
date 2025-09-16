package com.frcoding.audionotes.domain.repository

import com.frcoding.audionotes.domain.entity.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<Topic>>

    suspend fun searchTopics(query: String): List<Topic>

    suspend fun insertTopic(topic: Topic)

    suspend fun deleteTopic(topic: Topic)

    suspend fun getTopicsByIdList(topicIdList: List<Long>): List<Topic>
}