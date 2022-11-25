package com.ssinsirity.library

sealed class AuthStatus {
    object Idle : AuthStatus()
    object Loading : AuthStatus()
    object Success : AuthStatus()
    class Error(val throwable: Throwable?) : AuthStatus()
}