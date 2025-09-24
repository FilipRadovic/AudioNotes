package com.frcoding.audionotes.presentation.core.utils

import java.io.File

class AmplitudeCalculator(
    private val amplitudeLogFilePath: String,
    private val trackWidth: Float,
    private val amplitudeWidth: Float,
    spacing: Float
) {
    private val barCount = (trackWidth / (amplitudeWidth + spacing)).toInt()

    fun correctedSpacing(): Float {
        return (trackWidth - (barCount * amplitudeWidth)) / (barCount - 1)
    }

    fun heightCoefficients(): List<Float> {
        val amplitudeList = readAmplitudeLogFile()
        if (amplitudeList.isEmpty()) return List(barCount) { 0f }

        return if (amplitudeList.size >= barCount) {
            val segmentSize = amplitudeList.size / barCount
            amplitudeList.chunked(segmentSize) { segment ->
                segment.average().toFloat()
            }.take(barCount)
        } else {
            interpolateAmplitudes(amplitudeList, barCount)
        }
    }

    private fun readAmplitudeLogFile(): List<Float> {
        val file = File(amplitudeLogFilePath)

        if (!file.exists()) {
            throw IllegalArgumentException("Amplitude log file not found at path: $amplitudeLogFilePath")
        }

        return file.readText()
            .split(",")
            .filter { it.isNotBlank() }
            .map { amplitude ->
                amplitude.toFloatOrNull() ?: throw NumberFormatException("Invalid amplitude value: $amplitude")
            }
    }

    private fun interpolateAmplitudes(amplitudesList: List<Float>, targetSize: Int) : List<Float> {
        val interpolated = mutableListOf<Float>()
        val step = (amplitudesList.size - 1).toFloat() / (targetSize - 1)

        for (i in 0 until targetSize) {
            val position = i * step
            val index = position.toInt()
            val nextIndex = (index + 1).coerceAtMost(amplitudesList.size - 1)
            val fraction = position - index
            val value = amplitudesList[index] + fraction * (amplitudesList[nextIndex] - amplitudesList[index])
            interpolated.add(value)
        }

        return interpolated
    }
}