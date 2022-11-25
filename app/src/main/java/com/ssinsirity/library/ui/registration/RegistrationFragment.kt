package com.ssinsirity.library.ui.registration

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentRegistrationBinding
import com.ssinsirity.library.util.ViewModelField
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    private val viewModel by viewModels<RegistrationViewModel>()
    private val binding by viewBinding(FragmentRegistrationBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpCollectors()
    }

    private fun setUpListeners() {
        with(binding) {
            emailEt.addTextChangedListener(
                beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.EMAIL) },
                afterTextChanged = { editable ->
                    val email = editable.toString()
                    viewModel.update(ViewModelField.EMAIL, email)
                }
            )
            passwordEt.addTextChangedListener(
                beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.PASSWORD) },
                afterTextChanged = { editable ->
                    val password = editable.toString()
                    viewModel.update(ViewModelField.PASSWORD, password)
                }
            )
            repeatedPasswordEt.addTextChangedListener(
                beforeTextChanged = { _, _, _, _ -> viewModel.beforeChanged(ViewModelField.REPEATED_PASSWORD) },
                afterTextChanged = { editable ->
                    val repeatedPassword = editable.toString()
                    viewModel.update(ViewModelField.REPEATED_PASSWORD, repeatedPassword)
                }
            )
            submitBtn.setOnClickListener {
                val userInfo =
                    RegistrationFragmentDirections.registrationToUserInfo(viewModel.email.value)
                navController.navigate(userInfo)
                // todo viewModel.submitData()
            }
        }
    }

    private fun setUpCollectors() {
        repeatOnStart {
            viewModel.emailError.collect { emailError ->
                binding.emailLayout.error = emailError
            }
        }
        repeatOnStart {
            viewModel.passwordError.collect { emailError ->
                binding.passwordLayout.error = emailError
            }
        }
        repeatOnStart {
            viewModel.repeatedPasswordError.collect { emailError ->
                binding.repeatedPasswordLayout.error = emailError
            }
        }
        repeatOnStart {
            viewModel.authStatus.collect { status ->
                when (status) {
                    is AuthStatus.Idle -> {}
                    is AuthStatus.Loading -> setViewEnabled(false, errorMsg = null)
                    is AuthStatus.Success -> {
                        val userInfo =
                            RegistrationFragmentDirections.registrationToUserInfo(viewModel.email.value)
                        navController.navigate(userInfo)
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
            arrayOf(emailEt, passwordEt, repeatedPasswordEt, submitBtn).forEach { view ->
                view.isEnabled = enabled
            }
            registrationError.text = errorMsg
        }
    }
}