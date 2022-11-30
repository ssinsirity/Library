package com.ssinsirity.core.data.model

import java.util.*

data class BookedCatalog(
    val id: String,
    val catalog: Catalog,
    val reader: ReaderCard,
    val librarian: Librarian,
    val bookedDate: Date,
    val returnDate: Date = Date(0),
    val actualReturnDate: Date = Date(0)
)