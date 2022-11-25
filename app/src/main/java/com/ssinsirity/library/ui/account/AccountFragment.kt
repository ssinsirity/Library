package com.ssinsirity.library.ui.account

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentAccountBinding
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {
    private val binding by viewBinding(FragmentAccountBinding::bind)
    private val viewModel by viewModels<AccountViewModel>()
    private val navController by lazy { findNavController() }
}