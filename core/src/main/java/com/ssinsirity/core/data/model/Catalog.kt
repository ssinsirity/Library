package com.ssinsirity.core.data.model

data class Catalog(
    val id: String,
    val book: Book,
    val publisher: Publisher,
    val amount: Int
)