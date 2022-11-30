package com.ssinsirity.core.repository

import com.ssinsirity.core.data.model.Catalog

interface CatalogRepository {
    suspend fun fetchCatalogs(onResult: (Result<List<Catalog>>) -> Unit)
    suspend fun postCatalog(catalog: Catalog, onResult: (Result<Catalog>) -> Unit)
    suspend fun updateCatalog(catalog: Catalog, amount: Int, onResult: (Result<Catalog>) -> Unit)
}