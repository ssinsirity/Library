package com.ssinsirity.core.usecase

interface PasswordValidator {
    fun validate(password: String): Result<String>

    class Default : PasswordValidator {
        override fun validate(password: String): Result<String> =
            if (password.isBlank())
                Result.failure(Exception("The password can't be blank"))
            else if (password.length !in 6..16)
                Result.failure(Exception("The password needs to consist of at least 6 characters"))
            else if (password.none { it.isLetter() } || password.none { it.isDigit() })
                Result.failure(Exception("The password needs to contain at least one letter and digit"))
            else Result.success(password)
    }
}