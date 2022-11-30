package com.ssinsirity.library.ui.catalog_creation

sealed class CreationStatus {
    object Idle : CreationStatus()
    object Success : CreationStatus()
    class Error(val exception: Throwable?) : CreationStatus()
}
