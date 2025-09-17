package com.frcoding.audionotes.data.repository

import com.frcoding.audionotes.data.database.TopicDao
import com.frcoding.audionotes.data.mappers.toTopic
import com.frcoding.audionotes.data.mappers.toTopicDb
import com.frcoding.audionotes.data.mappers.toTopics
import com.frcoding.audionotes.domain.entity.Topic
import com.frcoding.audionotes.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao
) : TopicRepository {
    override fun getTopics(): Flow<List<Topic>> = topicDao.getTopics().map { it.toTopics() }

    override suspend fun searchTopics(query: String): List<Topic> =
        topicDao.searchTopics(query).map { it.toTopic() }

    override suspend fun insertTopic(topic: Topic) = topicDao.insertTopic(topic.toTopicDb())

    override suspend fun deleteTopic(topic: Topic) = topicDao.deleteTopic(topic.toTopicDb())

    override suspend fun getTopicsByIdList(topicIdList: List<Long>): List<Topic> {
        return topicDao.getTopicsByIdList(topicIdList).map { it.toTopic() }
    }
}