package com.ssinsirity.library.ui.catalog

sealed class CatalogStatus {
    object Success : CatalogStatus()
    object Error : CatalogStatus()
}
