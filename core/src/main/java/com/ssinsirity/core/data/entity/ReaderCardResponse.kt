package com.ssinsirity.core.data.entity

import com.google.firebase.firestore.DocumentId

data class ReaderCardResponse(
    @DocumentId var id: String = "",
    var email: String = "",
    var cardNumber: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = ""
)