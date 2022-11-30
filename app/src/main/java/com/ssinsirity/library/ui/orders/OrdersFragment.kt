package com.ssinsirity.library.ui.orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentOrdersBinding
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : Fragment(R.layout.fragment_orders) {
    private val binding by viewBinding(FragmentOrdersBinding::bind)
    private val viewModel by viewModels<OrdersViewModel>()
    private val navController by lazy { findNavController() }
    private val ordersAdapter = OrdersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpCollectors()
    }

    private fun setUpView() = with(binding) {
        ordersRv.adapter = ordersAdapter
        ordersSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterOrders(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterOrders(newText)
                return false
            }
        })
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.filteredOrders.collect { ordersAdapter.updateItems(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchOrders()
    }
}