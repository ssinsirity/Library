package com.ssinsirity.library.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.UserCredentials
import com.ssinsirity.core.repository.AuthManager
import com.ssinsirity.core.usecase.EmailValidator
import com.ssinsirity.core.usecase.PasswordValidator
import com.ssinsirity.core.usecase.RepeatedPasswordValidator
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.util.ViewModelField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val repeatedPasswordValidator: RepeatedPasswordValidator,
    private val authManager: AuthManager
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _repeatedPassword = MutableStateFlow("")
    val repeatedPassword = _repeatedPassword.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _repeatedPasswordError = MutableStateFlow<String?>(null)
    val repeatedPasswordError = _repeatedPasswordError.asStateFlow()

    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus = _authStatus.asStateFlow()

    fun update(field: ViewModelField, value: Any?) {
        when (field) {
            ViewModelField.EMAIL -> _email.update { value as String }
            ViewModelField.PASSWORD -> _password.update { value as String }
            ViewModelField.REPEATED_PASSWORD -> _repeatedPassword.update { value as String }
            else -> {}
        }
    }

    fun beforeChanged(field: ViewModelField) {
        when (field) {
            ViewModelField.EMAIL -> _emailError.update { null }
            ViewModelField.PASSWORD -> _passwordError.update { null }
            ViewModelField.REPEATED_PASSWORD -> _repeatedPasswordError.update { null }
            else -> {}
        }
        _authStatus.update { AuthStatus.Idle }
    }

    fun submitData() {
        val emailResult = emailValidator.validate(_email.value)
        val passwordResult = passwordValidator.validate(_password.value)
        val repeatedPasswordResult = repeatedPasswordValidator.validate(
            _password.value, _repeatedPassword.value
        )

        mapOf(
            _emailError to emailResult,
            _passwordError to passwordResult,
            _repeatedPasswordError to repeatedPasswordResult
        ).forEach { (error, result) ->
            error.update { result.exceptionOrNull()?.message }
        }

        val hasNotError =
            emailResult.isSuccess && passwordResult.isSuccess && repeatedPasswordResult.isSuccess

        if (hasNotError)
            register()
    }

    private fun register() {
        val email = _email.value
        val password = _password.value
        val credentials = UserCredentials(email, password)

        viewModelScope.launch {
            _authStatus.update { AuthStatus.Loading }

            authManager.register(credentials) { result ->
                _authStatus.update {
                    if (result.isSuccess)
                        AuthStatus.Success
                    else
                        AuthStatus.Error(result.exceptionOrNull())
                }
            }
        }
    }
}