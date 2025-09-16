package com.frcoding.audionotes.data.mappers

import com.frcoding.audionotes.data.entity.TopicDb
import com.frcoding.audionotes.domain.entity.Topic

fun Topic.toTopicDb(): TopicDb = TopicDb(
    id = id,
    name = name
)

fun TopicDb.toTopic(): Topic = Topic(
    id = id,
    name = name
)

fun List<TopicDb>.toTopics(): List<Topic> = map { it.toTopic() }