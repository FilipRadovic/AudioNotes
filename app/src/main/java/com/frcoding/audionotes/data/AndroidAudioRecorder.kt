package com.frcoding.audionotes.data

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.frcoding.audionotes.domain.audio.AudioRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.log10

class AndroidAudioRecorder @Inject constructor(
    private val context: Context
) : AudioRecorder {

    private val outputDir = context.filesDir
    private var audioFile: File? = null
    private var amplitudeLogFile: File? = null
    private var recorder: MediaRecorder? = null
    private var isCurrentlyRecording: Boolean = false
    private var isCurrentlyPaused: Boolean = false
    private var amplitudeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start() {
        val audioFileName = "temp_${System.currentTimeMillis()}.mp3"
        val amplitudeLogFileName = "temp_log_${System.currentTimeMillis()}.txt"
        audioFile = File(outputDir, audioFileName)
        amplitudeLogFile = File(outputDir, amplitudeLogFileName)

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)

            prepare()
            start()

            recorder = this
        }

        isCurrentlyRecording = true
        isCurrentlyPaused = false
        startLoggingAmplitude()
    }

    override fun pause() {
        recorder?.pause() ?: checkRecorderInitialized()
        isCurrentlyPaused = true
    }

    override fun stop(saveFile: Boolean): String {
        amplitudeJob?.cancel()
        recorder?.apply {
            stop()
            reset()
            release()
        } ?: checkRecorderInitialized()

        recorder = null
        isCurrentlyRecording = false

        audioFile?.let { file ->
            if (!saveFile) {
                file.delete()
                amplitudeLogFile?.delete()
                return ""
            } else {
                return file.absolutePath
            }
        } ?: throw IllegalStateException("Audio file was not created.")
    }

    override fun resume() {
        recorder?.resume() ?: checkRecorderInitialized()
        isCurrentlyPaused = false
    }

    override fun getAmplitudeLogFilePath(): String {
        amplitudeLogFile?.let { file ->
            return file.absolutePath
        } ?: throw IllegalStateException("Amplitude log file was not created.")
    }

    private fun startLoggingAmplitude() {
        amplitudeJob?.cancel()
        amplitudeJob = scope.launch {
            while (isActive && isRecording()) {
                if (!isPaused()) {
                    val amplitude = recorder?.maxAmplitude?.toFloat() ?: 0f
                    if (amplitude > 0) {
                        val normalized = (log10(amplitude + 1) / log10(32767f + 1)).coerceIn(0f, 1f)
                        amplitudeLogFile?.appendText("$normalized,")
                    }
                }
                delay(100)
            }
        }
    }

    private fun checkRecorderInitialized() {
        recorder ?: throw IllegalStateException("Recorder is not initialized. Call 'start()' first.")
    }

    private fun isRecording(): Boolean = isCurrentlyRecording
    private fun isPaused(): Boolean = isCurrentlyPaused

}