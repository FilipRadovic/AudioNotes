package com.frcoding.audionotes.domain.audio

interface AudioRecorder {
    fun start()

    fun pause()

    fun stop(saveFile: Boolean): String

    fun resume()

    fun getAmplitudeLogFilePath(): String
}