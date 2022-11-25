package com.ssinsirity.core.data.entity

import com.google.firebase.firestore.DocumentId

data class PublisherResponse(
    @DocumentId var id: String = "",
    var name: String = ""
)