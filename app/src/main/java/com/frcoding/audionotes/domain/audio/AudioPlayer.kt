package com.frcoding.audionotes.domain.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val currentPositionFlow: StateFlow<Int>

    fun initializeFile(filePath: String)

    fun play()

    fun pause()

    fun resume()

    fun stop()

    fun setOnCompleteListener(listener: () -> Unit)

    fun getDuration(): Int

    fun isPlaying(): Boolean
}