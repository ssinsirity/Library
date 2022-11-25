package com.ssinsirity.library.util

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

fun Instant.toDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.mm.yyyy", Locale.getDefault())
    return formatter.format(this)
}
