package com.ssinsirity.library.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.R
import com.ssinsirity.library.databinding.FragmentLoginBinding
import com.ssinsirity.library.util.ViewModelField
import com.ssinsirity.library.util.repeatOnStart
import com.ssinsirity.library.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel by viewModels<LoginViewModel>()
    private val binding by viewBinding(FragmentLoginBinding::bind)
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
            submitBtn.setOnClickListener { viewModel.submitData() }
            registerBtn.setOnClickListener {
                val registrationFragment = LoginFragmentDirections.loginToRegistration()
                navController.navigate(registrationFragment)
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
            viewModel.authStatus.collect { status ->
                when (status) {
                    is AuthStatus.Idle -> {}
                    is AuthStatus.Loading -> setViewEnabled(false, errorMsg = null)
                    is AuthStatus.Success -> viewModel.identifyUser()
                    is AuthStatus.Error -> {
                        val message = status.throwable?.message
                            ?: getString(R.string.something_gets_wrong)
                        setViewEnabled(true, errorMsg = message)
                    }
                }
            }
        }
        repeatOnStart {
            viewModel.ok.collect {
                if (it == "ok") {
                    val contentHost = LoginFragmentDirections.loginToContentHost()
                    navController.navigate(contentHost)
                }
            }
        }
    }

    private fun setViewEnabled(enabled: Boolean, errorMsg: String?) {
        with(binding) {
            arrayOf(emailEt, passwordEt, submitBtn).forEach { view ->
                view.isEnabled = enabled
            }
            loginError.text = errorMsg
        }
    }
}