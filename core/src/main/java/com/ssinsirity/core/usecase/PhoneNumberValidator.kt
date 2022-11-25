package com.ssinsirity.core.usecase

import android.util.Patterns

interface PhoneNumberValidator {
    fun validate(phoneNumber: String): Result<String>

    class Default : PhoneNumberValidator {
        override fun validate(phoneNumber: String): Result<String> =
            if (phoneNumber.isBlank())
                Result.failure(Exception("The phone number can't be blank"))
            else if (!Patterns.PHONE.matcher(phoneNumber).matches())
                Result.failure(Exception("That's not a valid phone number"))
            else
                Result.success(phoneNumber)
    }
}