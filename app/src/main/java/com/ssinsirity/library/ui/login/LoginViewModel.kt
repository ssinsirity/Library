package com.ssinsirity.library.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.ssinsirity.core.constants.Collections
import com.ssinsirity.core.data.UserCredentials
import com.ssinsirity.core.data.entity.ReaderCardResponse
import com.ssinsirity.core.data.toModel
import com.ssinsirity.core.repository.AuthManager
import com.ssinsirity.core.repository.UserSharedPref
import com.ssinsirity.core.usecase.EmailValidator
import com.ssinsirity.core.usecase.PasswordValidator
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.util.UserMode
import com.ssinsirity.library.util.ViewModelField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authManager: AuthManager,
    private val userSharedPref: UserSharedPref
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus = _authStatus.asStateFlow()

    private val _ok = MutableStateFlow("")
    val ok = _ok.asStateFlow()

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun update(field: ViewModelField, value: Any?) {
        when (field) {
            ViewModelField.EMAIL -> _email.update { value as String }
            ViewModelField.PASSWORD -> _password.update { value as String }
            else -> {}
        }
    }

    fun beforeChanged(field: ViewModelField) {
        when (field) {
            ViewModelField.EMAIL -> _emailError.update { null }
            ViewModelField.PASSWORD -> _passwordError.update { null }
            else -> {}
        }
        _authStatus.update { AuthStatus.Idle }
    }

    fun submitData() {
        val emailResult = emailValidator.validate(_email.value)
        val passwordResult = passwordValidator.validate(_password.value)

        mapOf(
            _emailError to emailResult,
            _passwordError to passwordResult,
        ).forEach { (error, result) ->
            error.update { result.exceptionOrNull()?.message }
        }

        val hasNotError = emailResult.isSuccess && passwordResult.isSuccess

        if (hasNotError)
            login()
    }

    private fun login() {
        val email = _email.value
        val password = _password.value
        val credentials = UserCredentials(email, password)

        viewModelScope.launch {
            _authStatus.update { AuthStatus.Loading }

            authManager.login(credentials) { result ->
                _authStatus.update {
                    if (result.isSuccess)
                        AuthStatus.Success
                    else
                        AuthStatus.Error(result.exceptionOrNull())
                }
            }
        }
    }

    fun identifyUser() {
        val email = _email.value
        viewModelScope.launch {
            var message = "ok"
            UserMode.mode = when {
                isReader(email) -> UserMode.Mode.READER
                isLibrarian(email) -> UserMode.Mode.LIBRARIAN
                else -> {
                    message = "error"
                    UserMode.Mode.ANONYMOUS
                }
            }
            _ok.update { message }
        }
    }

    private suspend fun isReader(email: String): Boolean {
        val foundReader = firestore.collection(Collections.READER)
            .whereEqualTo("email", email)
            .get().await().toObjects<ReaderCardResponse>().map { it.toModel() }

        if (foundReader.isNotEmpty()) {
            val reader = foundReader.first()
            userSharedPref.currentReader = reader
        }

        return foundReader.isNotEmpty()
    }

    private suspend fun isLibrarian(email: String): Boolean {
        val foundLibrarian = firestore.collection(Collections.LIBRARIAN)
            .whereEqualTo("email", email)
            .get().await()

        return !foundLibrarian.isEmpty
    }
}