package com.frcoding.audionotes.data.repository

import com.frcoding.audionotes.data.database.EntryDao
import com.frcoding.audionotes.data.mappers.toEntries
import com.frcoding.audionotes.data.mappers.toEntryDb
import com.frcoding.audionotes.domain.entity.Entry
import com.frcoding.audionotes.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EntryRepositoryImpl @Inject constructor(
    private val entryDao: EntryDao
) : EntryRepository {
    override fun getEntries(): Flow<List<Entry>> = entryDao.getEntries().map { it.toEntries() }

    override suspend fun upsertEntry(entry: Entry) = entryDao.upsertEntries(entry.toEntryDb())

    override suspend fun deleteEntry(entry: Entry) = entryDao.deleteEntry(entry.toEntryDb())
}