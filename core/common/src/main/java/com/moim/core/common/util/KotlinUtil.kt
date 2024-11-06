package com.moim.core.common.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

fun Float.toDecimalFloat(index: Int = 1, roundingMode: RoundingMode = RoundingMode.HALF_UP): Float {
    return BigDecimal(this.toDouble()).setScale(index, roundingMode).toFloat()
}

fun Float.toDecimalString(format: String = "##.#"): String {
    return DecimalFormat(format).format(this)
}