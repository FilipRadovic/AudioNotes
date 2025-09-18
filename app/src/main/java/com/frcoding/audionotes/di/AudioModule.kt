package com.frcoding.audionotes.di

import android.content.Context
import com.frcoding.audionotes.data.AndroidAudioPlayer
import com.frcoding.audionotes.data.AndroidAudioRecorder
import com.frcoding.audionotes.domain.audio.AudioPlayer
import com.frcoding.audionotes.domain.audio.AudioRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {
    @Provides
    @Singleton
    fun provideAudioRecorder(
        @ApplicationContext context: Context
    ) : AudioRecorder {
        return AndroidAudioRecorder(context)
    }

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @ApplicationContext context: Context
    ) : AudioPlayer {
        return AndroidAudioPlayer(context)
    }
}