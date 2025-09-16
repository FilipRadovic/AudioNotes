package com.frcoding.audionotes.data.mappers

import com.frcoding.audionotes.data.entity.EntryDb
import com.frcoding.audionotes.domain.entity.Entry

fun Entry.toEntryDb(): EntryDb = EntryDb(
    id = id,
    title = title,
    moodType = moodType,
    audioFilePath = audioFilePath,
    audioDuration = audioDuration,
    amplitudeLogFilePath = amplitudeLogFilePath,
    description = description,
    topics = topics,
    creationTimestamp = creationTimestamp
)

fun EntryDb.toEntry(): Entry = Entry(
    id = id,
    title = title,
    moodType = moodType,
    audioFilePath = audioFilePath,
    audioDuration = audioDuration,
    amplitudeLogFilePath = amplitudeLogFilePath,
    description = description,
    topics = topics,
    creationTimestamp = creationTimestamp
)

fun List<EntryDb>.toEntries(): List<Entry> = map { it.toEntry() }