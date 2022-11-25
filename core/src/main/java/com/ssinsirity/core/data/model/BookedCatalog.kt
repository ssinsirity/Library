package com.ssinsirity.core.data.model

import java.time.Instant

data class BookedCatalog(
    val id: String,
    val catalog: Catalog,
    val reader: ReaderCard,
    val librarian: Librarian,
    val bookedDate: Instant,
    val returnDate: Instant = Instant.MIN,
    val actualReturnDate: Instant = Instant.MIN
)