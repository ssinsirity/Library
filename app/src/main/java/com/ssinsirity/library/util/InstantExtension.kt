package com.ssinsirity.library.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toFormat(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(this)
}
