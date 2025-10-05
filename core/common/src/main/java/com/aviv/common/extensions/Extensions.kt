package com.aviv.common.extensions
import java.text.NumberFormat
import java.util.Locale

fun Double.formatPrice(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    return format.format(this)
}
