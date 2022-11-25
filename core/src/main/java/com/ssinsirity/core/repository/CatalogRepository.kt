package com.ssinsirity.core.repository

import com.ssinsirity.core.data.model.Catalog

interface CatalogRepository {
    suspend fun fetchCatalogs(onResult: (Result<List<Catalog>>) -> Unit)
    suspend fun postCatalog(catalog: Catalog, onResult: (Result<Catalog>) -> Unit)
    suspend fun postCatalogs(
        catalogs: Collection<Catalog>,
        onResult: (Result<Collection<Catalog>>) -> Unit
    )
}