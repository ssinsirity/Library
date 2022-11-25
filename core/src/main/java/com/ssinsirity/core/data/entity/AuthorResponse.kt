package com.ssinsirity.core.data.entity

import com.google.firebase.firestore.DocumentId

data class AuthorResponse(
    @DocumentId var id: String = "",
    var firstName: String = "",
    var lastName: String = ""
)