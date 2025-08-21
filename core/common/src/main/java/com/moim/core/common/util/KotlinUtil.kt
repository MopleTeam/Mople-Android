package com.moim.core.common.util

import kotlinx.coroutines.Job
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

fun Float.toDecimalFloat(index: Int = 1, roundingMode: RoundingMode = RoundingMode.HALF_UP): Float {
    return BigDecimal(this.toDouble()).setScale(index, roundingMode).toFloat()
}

fun Int.decimalFormatString(format: String = "###,###"): String {
    return DecimalFormat(format).format(this)
}

fun Float.decimalFormatString(format: String = "##.#"): String {
    return DecimalFormat(format).format(this)
}

fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}