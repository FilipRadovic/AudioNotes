package com.frcoding.audionotes.domain.repository

import com.frcoding.audionotes.domain.entity.Entry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun getEntries(): Flow<List<Entry>>

    suspend fun upsertEntry(entry: Entry)

    suspend fun deleteEntry(entry: Entry)
}