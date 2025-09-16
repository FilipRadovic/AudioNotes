package com.frcoding.audionotes.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.frcoding.audionotes.data.entity.EntryDb
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY creationTimestamp DESC")
    fun getEntries(): Flow<List<EntryDb>>

    @Upsert
    suspend fun upsertEntries(entryDb: EntryDb)

    @Delete
    suspend fun deleteEntry(entryDb: EntryDb)
}