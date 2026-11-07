package com.moim.core.ui.util

import kotlinx.coroutines.Job
import java.text.DecimalFormat

fun Int.decimalFormatString(format: String = "###,###"): String = DecimalFormat(format).format(this)

fun Float.decimalFormatString(format: String = "##.#"): String = DecimalFormat(format).format(this)

fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}

fun Job?.isActiveCheck(): Boolean = this?.isActive == true
