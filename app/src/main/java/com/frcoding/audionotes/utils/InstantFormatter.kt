package com.frcoding.audionotes.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object InstantFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val englishLocal: Locale = Locale("en", "US")

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatRelativeToDay(instant: Instant): String {
        val today = LocalDate.now(zoneId)
        val yesterday = today.minusDays(1)

        return when (val date = instant.atZone(zoneId).toLocalDate()) {
            today -> "TODAY"
            yesterday -> "YESTERDAY"
            else -> date.format(DateTimeFormatter.ofPattern("EEEE, MMM d", englishLocal))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatHoursAndMinutes(instant: Instant): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return instant.atZone(zoneId).toLocalDate().format(timeFormatter)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun formatMillisToTime(timeMillis: Long): String {
        val durationInSeconds = timeMillis / 1000
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            zoneId
        )
        val pattern = if (durationInSeconds < 3600) "ss:SS" else "mm:ss:SS"
        val formatter = DateTimeFormatter.ofPattern(
            pattern,
            Locale.getDefault()
        )
        return localDateTime.format(formatter)
    }
}
