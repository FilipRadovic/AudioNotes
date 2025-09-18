package com.frcoding.audionotes.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.frcoding.audionotes.data.database.EntryDao
import com.frcoding.audionotes.data.database.EntryDatabase
import com.frcoding.audionotes.data.database.TopicDao
import com.frcoding.audionotes.data.database.TopicDatabase
import com.frcoding.audionotes.data.repository.EntryRepositoryImpl
import com.frcoding.audionotes.data.repository.SettingsRepositoryImpl
import com.frcoding.audionotes.data.repository.TopicRepositoryImpl
import com.frcoding.audionotes.domain.repository.EntryRepository
import com.frcoding.audionotes.domain.repository.SettingsRepository
import com.frcoding.audionotes.domain.repository.TopicRepository
import com.frcoding.audionotes.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {
    @Provides
    @Singleton
    fun provideTopicDatabase(
        @ApplicationContext context: Context
    ) : TopicDatabase {
        return Room.databaseBuilder(
            context,
            TopicDatabase::class.java,
            Constants.TOPIC_DB
        ).build()
    }

    @Provides
    @Singleton
    fun provideEntryDatabase(
        @ApplicationContext context: Context
    ) : EntryDatabase {
        return Room.databaseBuilder(
            context,
            EntryDatabase::class.java,
            Constants.ENTRY_DB
        ).build()
    }

    @Provides
    @Singleton
    fun provideTopicDao(database: TopicDatabase): TopicDao = database.getTopicDao()

    @Provides
    fun provideTopicRepository(
        topicDao: TopicDao
    ): TopicRepository = TopicRepositoryImpl(topicDao)

    @Provides
    @Singleton
    fun provideEntryDao(database: EntryDatabase): EntryDao = database.getEntryDao()

    @Provides
    fun provideEntryRepository(
        entryDao: EntryDao
    ): EntryRepository = EntryRepositoryImpl(entryDao)

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(Constants.APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences
    ): SettingsRepository = SettingsRepositoryImpl(sharedPreferences)
}