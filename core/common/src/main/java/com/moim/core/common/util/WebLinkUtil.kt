package com.moim.core.common.util

import android.net.Uri
import android.util.Patterns
import androidx.core.net.toUri

private data class LinkInfo(
    val start: Int,
    val end: Int,
    val url: String
)

fun String.parseTextWithLinks(): List<Pair<Boolean, String>> {
    val tldPattern = Regex("\\.(com|net|org|kr|co\\.kr|ne\\.kr|edu|gov|info|biz|io|ai|app|dev)")
    val result = mutableListOf<Pair<Boolean, String>>()
    val matcher = Patterns.WEB_URL.matcher(this)
    val linkInfos = mutableListOf<LinkInfo>()

    // 1. 모든 웹링크의 위치와 정리된 URL 수집
    while (matcher.find()) {
        val originalUrl = matcher.group()
        val matchStart = matcher.start()
        val matchEnd = matcher.end()

        // 실제 URL의 시작점 찾기
        val actualUrlStart = findActualUrlStart(matchStart, originalUrl)
        val actualUrl = this.substring(actualUrlStart, matchEnd)

        val tldMatch = tldPattern.find(actualUrl)
        val cleanUrl = tldMatch?.let { match ->
            val endOfTld = match.range.last + 1
            val remaining = actualUrl.substring(endOfTld)

            // 한국어 문자가 포함되어 있으면 TLD까지만 자르기
            if (remaining.contains(Regex("[가-힣]"))) {
                actualUrl.substring(0, endOfTld)
            } else {
                actualUrl // 경로나 쿼리 파라미터는 유지
            }
        } ?: actualUrl

        // 실제로 사용할 링크의 끝 위치 계산
        val actualEnd = if (cleanUrl.length < actualUrl.length) {
            actualUrlStart + cleanUrl.length
        } else {
            matchEnd
        }

        linkInfos.add(LinkInfo(actualUrlStart, actualEnd, cleanUrl))
    }

    // 2. 겹치는 링크 제거 및 정렬
    val sortedLinks = linkInfos
        .distinctBy { it.url }
        .sortedBy { it.start }

    // 3. 문자열을 순회하면서 웹링크와 일반 텍스트 구분
    var currentIndex = 0

    for (linkInfo in sortedLinks) {
        // 링크 이전의 일반 텍스트 추가
        if (currentIndex < linkInfo.start) {
            val normalText = this.substring(currentIndex, linkInfo.start)
            if (normalText.isNotEmpty()) {
                result.add(Pair(false, normalText))
            }
        }

        // 웹링크 추가
        result.add(Pair(true, linkInfo.url))
        currentIndex = linkInfo.end
    }

    // 마지막 남은 일반 텍스트 추가
    if (currentIndex < this.length) {
        val remainingText = this.substring(currentIndex)
        if (remainingText.isNotEmpty()) {
            result.add(Pair(false, remainingText))
        }
    }

    // 만약 웹링크가 없다면 전체를 일반 텍스트로 반환
    if (result.isEmpty()) {
        result.add(Pair(false, this))
    }

    return result
}

private fun findActualUrlStart(matchStart: Int, matchedUrl: String): Int {
    // http:// 또는 https://로 시작하는 경우
    val httpIndex = matchedUrl.indexOf("http://")
    val httpsIndex = matchedUrl.indexOf("https://")

    when {
        httpIndex >= 0 -> return matchStart + httpIndex
        httpsIndex >= 0 -> return matchStart + httpsIndex
    }

    // www.로 시작하는 경우
    val wwwIndex = matchedUrl.indexOf("www.")
    if (wwwIndex >= 0) {
        return matchStart + wwwIndex
    }

    // 도메인 패턴으로 시작점 찾기 (영문자 + 숫자 + 하이픈으로 구성된 도메인)
    val domainPattern = Regex("[a-zA-Z0-9][a-zA-Z0-9-]*\\.[a-zA-Z]{2,}")
    val domainMatch = domainPattern.find(matchedUrl)

    if (domainMatch != null) {
        return matchStart + domainMatch.range.first
    }

    // 기본값: 원래 시작점 반환
    return matchStart
}

fun String.toValidUrl(): Uri {
    return when {
        this.startsWith("http://") || this.startsWith("https://") -> this
        this.startsWith("www.") -> "https://$this"
        else -> "https://www.$this"
    }.toUri()
}