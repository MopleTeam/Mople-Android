package com.moim.core.common.model.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.encoding(encoding: String = StandardCharsets.UTF_8.toString()): String {
    return URLEncoder.encode(this, encoding)
}