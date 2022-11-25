package com.ssinsirity.core.data.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class BookedCatalogResponse(
    @DocumentId var id: String = "",
    var catalogId: String = "",
    var readerId: String = "",
    var librarianId: String = "",
    var bookedDate: Timestamp = Timestamp.now(),
    var returnDate: Timestamp = Timestamp.now(),
    var actualReturnDate: Timestamp = Timestamp.now()
)