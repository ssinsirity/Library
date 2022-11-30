package com.ssinsirity.library.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.model.BookedCatalog
import com.ssinsirity.core.repository.BookedCatalogRepository
import com.ssinsirity.core.repository.UserSharedPref
import com.ssinsirity.library.util.UserMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    userSharedPref: UserSharedPref,
    private val bookedCatalogRepository: BookedCatalogRepository
) : ViewModel() {

    private var _userId: String? = null
    private var _orders = emptyList<BookedCatalog>()

    private val _filteredOrders = MutableStateFlow(emptyList<BookedCatalog>())
    val filteredOrders = _filteredOrders.asStateFlow()

    init {
        val userId = if (UserMode.mode == UserMode.Mode.READER)
            userSharedPref.currentReader.id
        else null
        fetchOrders(userId)
    }

    fun fetchOrders() {
        fetchOrders(_userId)
    }

    private fun fetchOrders(userId: String?) {
        viewModelScope.launch {
            bookedCatalogRepository.fetchBookedCatalogs(userId) { result ->
                if (result.isSuccess) {
                    val ordersResult = result.getOrDefault(emptyList())

                    _orders = ordersResult
                    _filteredOrders.update { ordersResult }
                }
            }
        }
    }

    fun filterOrders(q: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val query = q ?: ""
            val filteredItems = _orders.filter { order ->
                arrayOf(
                    order.catalog.book.title,
                    order.reader.cardNumber,
                    "${order.reader.firstName} ${order.reader.lastName}"
                ).any { it.contains(query, ignoreCase = true) }
            }
            _filteredOrders.update { filteredItems }
        }
    }
}