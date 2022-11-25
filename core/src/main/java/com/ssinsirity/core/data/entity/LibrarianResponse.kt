package com.ssinsirity.core.data.entity

import com.google.firebase.firestore.DocumentId

data class LibrarianResponse(
    @DocumentId var id: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = ""
)