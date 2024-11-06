package com.moim.core.common.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getDateTimeFormatString(dateTime: String?, pattern: String): String {
    val zonedDateTime = dateTime?.let { ZonedDateTime.parse(dateTime) } ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
}

fun getDateTimeFormatZoneDate(dateTime: ZonedDateTime? = null, pattern: String): String {
    val zonedDateTime = dateTime ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())?.format(DateTimeFormatter.ofPattern(pattern)) ?: ""
}

fun getDateFormatLongTime(dateTime: String?): Long {
    val zonedDateTime = dateTime?.let { ZonedDateTime.parse(dateTime) } ?: ZonedDateTime.now()
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getStringFormatZonedDateTime(dateTime: String?): ZonedDateTime {
    val zonedDateTime = dateTime?.let { ZonedDateTime.parse(dateTime) } ?: ZonedDateTime.now()
    return zonedDateTime
}

fun getLongFormatZonedDateTime(epochMilli: Long): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
}

fun getZonedDateTimeDefault(zonedDateTime: ZonedDateTime = ZonedDateTime.now()): ZonedDateTime {
    return zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
}