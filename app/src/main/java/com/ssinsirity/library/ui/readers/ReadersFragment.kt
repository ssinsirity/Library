package com.ssinsirity.library.ui.readers

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentReadersBinding
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadersFragment : Fragment(R.layout.fragment_readers) {
    private val binding by viewBinding(FragmentReadersBinding::bind)
    private val viewModel by viewModels<ReadersViewModel>()
    private val navController by lazy { findNavController() }
    private val readersAdapter = ReadersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpCollectors()
    }

    private fun setUpView() = with(binding) {
        readersRv.adapter = readersAdapter
        readersSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterReaders(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterReaders(newText)
                return false
            }
        })
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.filteredReaders.collect { readersAdapter.updateItems(it) }
        }
    }
}