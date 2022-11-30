package com.ssinsirity.library.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.model.Catalog
import com.ssinsirity.core.repository.BookedCatalogRepository
import com.ssinsirity.core.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val bookedCatalogRepository: BookedCatalogRepository
) : ViewModel() {

    private var _catalogs = listOf<Catalog>()

    private val _filteredCatalogs = MutableStateFlow(emptyList<Catalog>())
    val filteredCatalogs = _filteredCatalogs.asStateFlow()


    init {
        fetchCatalogs()
    }

    fun fetchCatalogs() {
        viewModelScope.launch {
            catalogRepository.fetchCatalogs { result ->
                if (result.isSuccess) {
                    _catalogs = result.getOrDefault(emptyList())
                    _filteredCatalogs.update { _catalogs }
                }
            }
        }
    }

    fun filterCatalogs(q: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val query = q ?: ""
            val filteredItems = _catalogs.filter { catalog ->
                arrayOf(
                    catalog.book.title,
                    catalog.book.author.firstName,
                    catalog.book.author.lastName
                ).any { it.contains(query, ignoreCase = true) }
            }
            _filteredCatalogs.update { filteredItems }
        }
    }

    fun orderCatalog(catalog: Catalog) {
        viewModelScope.launch {
            bookedCatalogRepository.bookCatalog(catalog) {
                if(it.isSuccess) fetchCatalogs()
            }
        }
    }

    fun updateCatalogAmount(catalog: Catalog, amount: Int) {
        viewModelScope.launch {
            catalogRepository.updateCatalog(catalog, amount) {}
            fetchCatalogs()
        }
    }
}