package com.moim.core.common.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getDateTimeBetweenDay(
    startDate: ZonedDateTime? = null,
    endDate: ZonedDateTime? = null,
): Int {
    val startDateTime = (startDate ?: ZonedDateTime.now()).default()
    val endDateTime = (endDate ?: ZonedDateTime.now()).default()
    return Duration.between(startDateTime, endDateTime).toDays().toInt()
}

fun ZonedDateTime?.parseLongTime(): Long {
    val zonedDateTime = this ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun ZonedDateTime?.parseDateString(pattern: String): String =
    try {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        this?.format(formatter) ?: throw IllegalArgumentException()
    } catch (_: Exception) {
        this.toString()
    }

fun ZonedDateTime?.parseDateString(): String {
    val zonedDateTime = this ?: ZonedDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).format(formatter)
}

fun ZonedDateTime.default(): ZonedDateTime =
    this
        .withZoneSameInstant(ZoneId.systemDefault())
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

fun String?.parseZonedDateTime(): ZonedDateTime =
    try {
        val formatterWithTimezone = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")
        ZonedDateTime.parse("$this+09:00", formatterWithTimezone).withZoneSameInstant(ZoneId.systemDefault())
    } catch (_: Exception) {
        ZonedDateTime.now()
    }

fun String.parseDateStringToZonedDateTime(): ZonedDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate = LocalDate.parse(this, formatter)

    return localDate.atStartOfDay(ZoneId.systemDefault())
}

fun LocalDate.parseZonedDateTime(): ZonedDateTime {
    val zonedDateTime = this.atStartOfDay(ZoneId.systemDefault())
    return zonedDateTime
}

fun Long.parseZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
