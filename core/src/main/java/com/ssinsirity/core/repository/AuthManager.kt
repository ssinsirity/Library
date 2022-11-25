package com.ssinsirity.core.repository

import com.ssinsirity.core.data.UserCredentials

interface AuthManager {
    suspend fun register(credentials: UserCredentials, onResult: (Result<UserCredentials>) -> Unit)
    suspend fun login(credentials: UserCredentials, onResult: (Result<UserCredentials>) -> Unit)
    suspend fun logOut()
}