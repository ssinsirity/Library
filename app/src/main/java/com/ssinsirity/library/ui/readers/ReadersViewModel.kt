package com.ssinsirity.library.ui.readers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.model.ReaderCard
import com.ssinsirity.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var _readers = listOf<ReaderCard>()

    private val _filteredReaders = MutableStateFlow(emptyList<ReaderCard>())
    val filteredReaders = _filteredReaders.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.fetchReaders { result ->
                if (result.isSuccess) {
                    val readersResult = result.getOrDefault(emptyList())

                    _readers = readersResult
                    _filteredReaders.update { readersResult }
                }

            }
        }
    }

    fun filterReaders(q: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val query = q ?: ""
            val filteredItems = _readers.filter { reader ->
                arrayOf(
                    reader.cardNumber,
                    reader.phoneNumber,
                    "${reader.firstName} ${reader.lastName}"
                ).any { it.contains(query, ignoreCase = true) }
            }
            _filteredReaders.update { filteredItems }
        }
    }
}