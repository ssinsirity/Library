package com.ssinsirity.library.ui.registration

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentUserInfoBinding
import com.ssinsirity.library.util.ViewModelField
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoFragment : Fragment(R.layout.fragment_user_info) {
    private val binding by viewBinding(FragmentUserInfoBinding::bind)
    private val viewModel by viewModels<UserInfoViewModel>()
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpCollectors()
    }

    private fun setUpView() = with(binding) {
        nameEt.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.NAME) },
            afterTextChanged = { text ->
                val name = text.toString()
                viewModel.update(ViewModelField.NAME, name)
            }
        )
        surnameEt.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.SURNAME) },
            afterTextChanged = { text ->
                val surname = text.toString()
                viewModel.update(ViewModelField.SURNAME, surname)
            }
        )
        phoneNumberEt.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.PHONE_NUMBER) },
            afterTextChanged = { text ->
                val phoneNumber = text.toString()
                viewModel.update(ViewModelField.PHONE_NUMBER, phoneNumber)
            }
        )
        submitBtn.setOnClickListener { viewModel.submitData() }
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.nameError.collect { binding.nameLayout.error = it }
        }
        repeatOnStart {
            viewModel.surnameError.collect { binding.surnameLayout.error = it }
        }
        repeatOnStart {
            viewModel.phoneNumberError.collect { binding.phoneNumberLayout.error = it }
        }
        repeatOnStart {
            viewModel.authState.collect { status ->
                when (status) {
                    AuthStatus.Idle -> {}
                    AuthStatus.Loading -> setViewEnabled(false, errorMsg = null)
                    AuthStatus.Success -> {
                        val toContentHost = UserInfoFragmentDirections.userInfoToContentHost()
                        navController.navigate(toContentHost)
                    }
                    is AuthStatus.Error -> {
                        val message = status.throwable?.message
                            ?: getString(R.string.something_gets_wrong)
                        setViewEnabled(true, errorMsg = message)
                    }
                }
            }
        }
    }

    private fun setViewEnabled(enabled: Boolean, errorMsg: String?) {
        with(binding) {
            arrayOf(nameEt, surnameEt, phoneNumberEt, submitBtn).forEach { view ->
                view.isEnabled = enabled
            }
            userInfoError.text = errorMsg
        }
    }
}