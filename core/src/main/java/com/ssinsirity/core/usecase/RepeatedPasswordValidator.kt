package com.ssinsirity.core.usecase

interface RepeatedPasswordValidator {
    fun validate(password: String, repeatedPassword: String): Result<String>

    class Default : RepeatedPasswordValidator {
        override fun validate(password: String, repeatedPassword: String): Result<String> =
            if (password != repeatedPassword)
                Result.failure(Exception("The passwords don't match"))
            else
                Result.success(password)
    }
}