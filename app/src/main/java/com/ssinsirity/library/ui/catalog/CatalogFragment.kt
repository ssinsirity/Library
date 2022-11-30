package com.ssinsirity.library.ui.catalog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.core.data.model.Catalog
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentCatalogBinding
import com.ssinsirity.library.ui.bottom_nav.BottomNavFragmentDirections
import com.ssinsirity.library.util.UserMode
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    private val binding by viewBinding(FragmentCatalogBinding::bind)
    private val viewModel by viewModels<CatalogViewModel>()
    private val navController by lazy { findNavController() }
    private val parentNavController by lazy { parentFragment?.parentFragment?.findNavController() }
    private val catalogsAdapter = CatalogAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpCollectors()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCatalogs()
    }

    private fun setUpView() = with(binding) {
        catalogsRv.adapter = catalogsAdapter
        catalogsSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterCatalogs(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterCatalogs(newText)
                return false
            }
        })

        when (UserMode.mode) {
            UserMode.Mode.READER -> setUpReader()
            UserMode.Mode.LIBRARIAN -> setUpLibrarian()
            else -> {}
        }
    }

    private fun setUpReader() {
        catalogsAdapter.setOnClickListener { catalog ->
            if (catalog.amount > 0) showConfirmDialog(catalog) else showErrorDialog()
        }
    }


    private fun setUpLibrarian() = with(binding) {
        catalogsAdapter.setOnClickListener { catalog -> showUpdateAmountDialog(catalog) }

        createNewCatalogFab.isVisible = true
        createNewCatalogFab.setOnClickListener {
            val catalogCreation = BottomNavFragmentDirections.contentHostToCatalogCreation()
            parentNavController?.navigate(catalogCreation)
        }
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.filteredCatalogs.collect { catalogsAdapter.updateItems(it) }
        }
    }

    private fun showConfirmDialog(catalog: Catalog) {
        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.confirm_order)
            .setMessage(R.string.are_you_sure_wanna_order)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.orderCatalog(catalog)
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.not_available)
            .setMessage(R.string.please_choose_another_book)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showUpdateAmountDialog(catalog: Catalog) {
        val layoutInflater = LayoutInflater.from(context)
        val customView = layoutInflater.inflate(R.layout.view_new_amount_input, null)

        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setView(customView)
            .setTitle(R.string.update_amount_of_catalog)
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .apply {
                setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.update)) { _, _ ->
                    val amountInputEt = customView.findViewById<EditText>(R.id.amount_input_et)
                    val amount = amountInputEt.text.toString().toInt()
                    viewModel.updateCatalogAmount(catalog, amount)
                }
                show()
            }
    }
}