package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _messageResult = MutableLiveData<String>()

    val messageResult: MutableLiveData<String> get() = _messageResult

    private val _statusResult = MutableLiveData<Boolean>()

    val statusResult: MutableLiveData<Boolean> get() = _statusResult


    val oldPassword = MutableLiveData<String>()
    val newPassword =  MutableLiveData<String>()
    val newPasswordRepeat =  MutableLiveData<String>()

    fun resetPassword() {
        viewModelScope.launch {
            val result = dataRepository.apiChangePassword(oldPassword.value ?: "", newPassword.value ?: "", newPasswordRepeat.value ?: "")
            _messageResult.postValue(result.first ?: "")
            _statusResult.postValue(result.second ?: false)
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _messageResult.postValue("")
            _statusResult.postValue(false)
            oldPassword.value = ""
            newPassword.value = ""
            newPasswordRepeat.value = ""
        }
    }
}