package com.saneef.ratenotifier.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object DateUtils {

    fun formatDateTime(timestamp: Long): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            .toString()
    }
}
