package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import kotlinx.coroutines.launch

class PasswordResetViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _passwordResetMessageResult = MutableLiveData<String>()

    val passwordResetMessageResult: LiveData<String> get() = _passwordResetMessageResult

    private val _passwordResetSuccessResult = MutableLiveData<Boolean>()

    val passwordResetSuccessResult: LiveData<Boolean> get() = _passwordResetSuccessResult

    val email = MutableLiveData<String>()

    fun resetPassword () {
        viewModelScope.launch {
            val result = dataRepository.apiResetPassword(email.value ?: "")
            _passwordResetMessageResult.postValue(result.first ?: "")
            _passwordResetSuccessResult.postValue(result.second ?: false)
        }
    }

}