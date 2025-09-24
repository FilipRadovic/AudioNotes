package com.frcoding.audionotes.utils

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopWatch {
    private val _formattedTime = MutableStateFlow(Constants.DEFAULT_FORMATTED_TIME)
    val formattedTime: StateFlow<String> = _formattedTime

    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isRunning = false

    private var timeMillis = 0L
    private var lastTimestamp = 0L

    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {
        if (isRunning) return

        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            isRunning = true
            while (isRunning) {
                delay(10L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
                _formattedTime.value = InstantFormatter.formatMillisToTime(timeMillis)
            }
        }
    }

    fun pause() {
        isRunning = false
    }

    fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        _formattedTime.value = Constants.DEFAULT_FORMATTED_TIME
        isRunning = false
    }
}