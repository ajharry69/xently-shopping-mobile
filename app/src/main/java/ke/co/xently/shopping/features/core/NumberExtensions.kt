package ke.co.xently.shopping.features.core

import java.math.BigDecimal

fun Number.toStringWithoutUnnecessaryDigitsAfterDecimalPoint(): String {
    return if (this is BigDecimal) {
        toPlainString()
    } else {
        toString()
    }.toStringWithoutUnnecessaryDigitsAfterDecimalPoint()
}

fun String.toStringWithoutUnnecessaryDigitsAfterDecimalPoint(): String {
    return split('.', limit = 2).let { numberComponents ->
        if (numberComponents.size > 1) {
            val (whole, decimal) = numberComponents
            buildList {
                add(whole)
                decimal.replace("0+$".toRegex(), "")
                    .takeIf { it.isNotBlank() }
                    ?.let(::add)
            }
        } else {
            numberComponents
        }.joinToString(".")
    }
}