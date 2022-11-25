package com.ssinsirity.core.data.entity

import com.google.firebase.firestore.DocumentId

data class CatalogResponse(
    @DocumentId var id: String = "",
    var bookId: String = "",
    var publisherId: String = "",
    var amount: Int = 0
)