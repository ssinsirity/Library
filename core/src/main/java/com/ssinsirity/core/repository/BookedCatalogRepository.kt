package com.ssinsirity.core.repository

import com.ssinsirity.core.data.model.BookedCatalog
import com.ssinsirity.core.data.model.Catalog

interface BookedCatalogRepository {
    suspend fun fetchBookedCatalogs(
        userId: String?,
        onResult: (Result<List<BookedCatalog>>) -> Unit
    )

    suspend fun bookCatalog(catalog: Catalog, onResult: (Result<Catalog>) -> Unit)
}