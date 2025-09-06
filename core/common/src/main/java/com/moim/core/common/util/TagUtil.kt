package com.moim.core.common.util

fun createMentionTagMessage(mentionNames: List<String>, message: String): String {
    var result = message
    mentionNames.forEach { nickname ->
        val mentionPattern = "@${nickname}"
        result = result.replace(mentionPattern, "<mention>$mentionPattern</mention>")
    }

    return result
}

fun parseMentionTagMessage(mentionNames: List<String>, message: String): String {
    var result = message
    mentionNames.forEach { nickname ->
        val taggedMention = "<mention>@$nickname</mention>"
        val plainMention = "@${nickname}"
        result = result.replace(taggedMention, plainMention)
    }

    return result
}