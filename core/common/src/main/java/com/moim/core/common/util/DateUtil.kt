package com.moim.core.common.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getDateTimeFormatString(
    dateTime: String?,
    datePattern: String = "yyyy-MM-dd HH:mm:ssXXX",
    pattern: String
): String {
    return try {
        val formatterWithTimezone = DateTimeFormatter.ofPattern(datePattern)
        val zonedDateTimeWithTimezone = ZonedDateTime.parse("$dateTime+09:00", formatterWithTimezone)
        zonedDateTimeWithTimezone.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
    } catch (e: Exception) {
        dateTime ?: ""
    }
}

fun getDateTimeFormatZonedDate(dateTime: ZonedDateTime? = null, pattern: String): String {
    val zonedDateTime = dateTime ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
}

fun getDateFormatLongTime(dateTime: String?): Long {
    val zonedDateTime = dateTime?.let { ZonedDateTime.parse(dateTime) } ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getDateFormatLongTime(dateTime: ZonedDateTime?): Long {
    val zonedDateTime = dateTime ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getLongFormatZonedDateTime(epochMilli: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
}

fun getZonedDateTimeDefault(zonedDateTime: ZonedDateTime = ZonedDateTime.now()): ZonedDateTime {
    return zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
}

fun getDateTimeBetweenDay(startDate: ZonedDateTime? = null, endDate: ZonedDateTime? = null): Int {
    val startDateTime = (startDate ?: ZonedDateTime.now()).default()
    val endDateTime = (endDate ?: ZonedDateTime.now()).default()
    return Duration.between(startDateTime, endDateTime).toDays().toInt()
}

fun String?.parseZonedDateTime(): ZonedDateTime {
    val zonedDateTime = this?.let { ZonedDateTime.parse(this) } ?: ZonedDateTime.now()
    return zonedDateTime
}

fun ZonedDateTime?.parseDateString(): String {
    val zonedDateTime = this ?: ZonedDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    return zonedDateTime.format(formatter)
}

fun String?.parseZonedDateTimeForDateString() : ZonedDateTime {
    return try {
        val formatterWithTimezone = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")
        ZonedDateTime.parse("$this+09:00", formatterWithTimezone)
    } catch (e: Exception) {
        ZonedDateTime.now()
    }
}

fun LocalDate.parseZonedDateTime(): ZonedDateTime {
    val zonedDateTime = this.atStartOfDay(ZoneId.systemDefault())
    return zonedDateTime
}

fun ZonedDateTime.default(): ZonedDateTime {
    return this.withHour(0).withMinute(0).withSecond(0).withNano(0)
}