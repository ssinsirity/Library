package com.ssinsirity.library.ui.catalog_creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.model.*
import com.ssinsirity.core.repository.CatalogRepository
import com.ssinsirity.library.util.ViewModelField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CatalogCreationViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository
) : ViewModel() {
    private var _bookTitle = ""
    private var _genre = ""
    private var _authorFirstName = ""
    private var _authorLastName = ""
    private var _publisherName = ""
    private var _amount = 0

    private val _creationStatus = MutableStateFlow<CreationStatus>(CreationStatus.Idle)
    val creationStatus = _creationStatus.asStateFlow()


    fun update(field: ViewModelField, value: Any) {
        when (field) {
            ViewModelField.BOOK_TITLE -> _bookTitle = value as String
            ViewModelField.GENRE -> _genre = value as String
            ViewModelField.AUTHOR_FIRST_NAME -> _authorFirstName = value as String
            ViewModelField.AUTHOR_LAST_NAME -> _authorLastName = value as String
            ViewModelField.PUBLISHER_NAME -> _publisherName = value as String
            ViewModelField.AMOUNT -> _amount = value as Int
            else -> {}
        }
    }

    fun create() {
        val genre = Genre.findValue(_genre)
        val publisher = Publisher(
            id = UUID.randomUUID().toString(),
            name = _publisherName
        )
        val author = Author(
            id = UUID.randomUUID().toString(),
            firstName = _authorFirstName,
            lastName = _authorLastName
        )
        val book = Book(
            id = UUID.randomUUID().toString(),
            title = _bookTitle,
            annotation = "",
            author = author,
            genre = genre,
            published = Date()
        )
        val catalog = Catalog(
            id = UUID.randomUUID().toString(),
            book = book,
            publisher = publisher,
            amount = _amount
        )
        viewModelScope.launch {
            catalogRepository.postCatalog(catalog) { result ->
                _creationStatus.update {
                    if (result.isSuccess)
                        CreationStatus.Success
                    else
                        CreationStatus.Error(result.exceptionOrNull())
                }
            }
        }
    }
}