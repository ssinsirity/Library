package com.ssinsirity.core.usecase

interface NameValidator {
    fun validate(name: String): Result<String>

    class Default : NameValidator {
        override fun validate(name: String): Result<String> =
            if (name.isBlank() || name.length < 2)
                Result.failure(Exception("The name needs to consist of at least 2 characters"))
            else
                Result.success(name)
    }
}