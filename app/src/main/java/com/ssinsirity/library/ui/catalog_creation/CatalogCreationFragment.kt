package com.ssinsirity.library.ui.catalog_creation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentCatalogCreationBinding
import com.ssinsirity.library.util.ViewModelField
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogCreationFragment : Fragment(R.layout.fragment_catalog_creation) {
    private val binding by viewBinding(FragmentCatalogCreationBinding::bind)
    private val viewModel by viewModels<CatalogCreationViewModel>()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpCollectors()
    }

    private fun setUpView() = with(binding) {
        bookNameEt.addTextChangedListener { text ->
            val bookTitle = text.toString()
            viewModel.update(ViewModelField.BOOK_TITLE, bookTitle)
        }
        genreEt.addTextChangedListener { text ->
            val genre = text.toString()
            viewModel.update(ViewModelField.GENRE, genre)
        }
        authorNameEt.addTextChangedListener { text ->
            val authorFirstName = text.toString()
            viewModel.update(ViewModelField.AUTHOR_FIRST_NAME, authorFirstName)
        }
        authorSurnameEt.addTextChangedListener { text ->
            val authorLastName = text.toString()
            viewModel.update(ViewModelField.AUTHOR_LAST_NAME, authorLastName)
        }
        publisherEt.addTextChangedListener { text ->
            val publisher = text.toString()
            viewModel.update(ViewModelField.PUBLISHER_NAME, publisher)
        }
        amountEt.addTextChangedListener { text ->
            val amount = text.toString().toInt()
            viewModel.update(ViewModelField.AMOUNT, amount)
        }
        createBtn.setOnClickListener { viewModel.create() }
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.creationStatus.collect { status ->
                when (status) {
                    CreationStatus.Idle -> {}
                    CreationStatus.Success -> navController.navigateUp()
                    is CreationStatus.Error -> Toast.makeText(
                        context,
                        status.exception?.message ?: "Error happened",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}