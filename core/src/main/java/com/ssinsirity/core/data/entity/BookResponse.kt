package com.ssinsirity.core.data.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class BookResponse(
    @DocumentId var id: String = "",
    var title: String = "",
    var annotation: String = "",
    var publishedDate: Timestamp = Timestamp.now(),
    var authorId: String = "",
    var genreId: String = ""
)