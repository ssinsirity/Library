package com.ssinsirity.core.usecase

import android.util.Patterns

interface EmailValidator {
    fun validate(email: String): Result<String>

    class Default : EmailValidator {
        override fun validate(email: String): Result<String> =
            if (email.isBlank())
                Result.failure(Exception("The email can't be blank"))
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                Result.failure(Exception("That's not a valid email"))
            else
                Result.success(email)
    }
}