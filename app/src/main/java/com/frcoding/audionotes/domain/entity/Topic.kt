package com.frcoding.audionotes.domain.entity

import com.frcoding.audionotes.utils.Constants

data class Topic(
    val id: Long = Constants.INITIAL_TOPIC_ID,
    val name: String
)