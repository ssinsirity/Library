package com.ssinsirity.core.data.model

import java.util.*

data class Book(
    val id: String,
    val title: String,
    val annotation: String,
    val author: Author,
    val genre: Genre,
    val published: Date
)