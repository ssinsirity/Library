package com.ssinsirity.library.ui.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssinsirity.core.data.model.Librarian
import com.ssinsirity.core.data.model.ReaderCard
import com.ssinsirity.core.repository.UserRepository
import com.ssinsirity.core.usecase.NameValidator
import com.ssinsirity.core.usecase.PhoneNumberValidator
import com.ssinsirity.library.AuthStatus
import com.ssinsirity.library.util.UserMode
import com.ssinsirity.library.util.ViewModelField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val nameValidator: NameValidator,
    private val phoneNumberValidator: PhoneNumberValidator,
    private val userRepository: UserRepository
) : ViewModel() {

    private val email: String

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _surname = MutableStateFlow("")
    val surname = _surname.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError = _nameError.asStateFlow()

    private val _surnameError = MutableStateFlow<String?>(null)
    val surnameError = _surnameError.asStateFlow()

    private val _phoneNumberError = MutableStateFlow<String?>(null)
    val phoneNumberError = _phoneNumberError.asStateFlow()

    private val _userType = MutableStateFlow(UserMode.Mode.READER)
    val userType = _userType.asStateFlow()

    private val _authState = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authState = _authState.asStateFlow()

    init {
        val userInfoArgs = UserInfoFragmentArgs.fromSavedStateHandle(savedStateHandle)
        email = userInfoArgs.userEmail
    }

    fun update(field: ViewModelField, value: Any?) {
        when (field) {
            ViewModelField.NAME -> _name.update { value as String }
            ViewModelField.SURNAME -> _surname.update { value as String }
            ViewModelField.PHONE_NUMBER -> _phoneNumber.update { value as String }
            else -> {}
        }
    }

    fun beforeChanged(field: ViewModelField) {
        when (field) {
            ViewModelField.NAME -> _nameError.update { null }
            ViewModelField.SURNAME -> _surnameError.update { null }
            ViewModelField.PHONE_NUMBER -> _phoneNumberError.update { null }
            else -> {}
        }
        _authState.update { AuthStatus.Idle }
    }

    fun submitData() {
        val nameResult = nameValidator.validate(_name.value)
        val surnameResult = nameValidator.validate(_surname.value)
        val phoneNumberResult = phoneNumberValidator.validate(_phoneNumber.value)

        mapOf(
            _nameError to nameResult,
            _surnameError to surnameResult,
            _phoneNumberError to phoneNumberResult
        ).forEach { (error, result) ->
            error.update { result.exceptionOrNull()?.message }
        }

        val hasNotError =
            nameResult.isSuccess && surnameResult.isSuccess && phoneNumberResult.isSuccess

        if (hasNotError)
            loadUser()
    }

    private fun loadUser() {
        val onResult = { result: Result<Any> ->
            _authState.update {
                if (result.isSuccess)
                    AuthStatus.Success
                else
                    AuthStatus.Error(result.exceptionOrNull())
            }
        }

        viewModelScope.launch {
            if (_userType.value == UserMode.Mode.LIBRARIAN) {
                val librarian = Librarian(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    firstName = _name.value,
                    lastName = _surname.value
                )
                userRepository.post(librarian, onResult)
            } else {
                val reader = ReaderCard(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    cardNumber = Random.nextInt(100000, 1000000).toString(),
                    firstName = _name.value,
                    lastName = _surname.value,
                    phoneNumber = _phoneNumber.value
                )
                userRepository.post(reader, onResult)
            }
        }
    }
}