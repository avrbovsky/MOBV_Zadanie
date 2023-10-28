package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangePasswordViewModel: ViewModel() {
    private val _oldPassword = MutableLiveData<String>()
    private val _newPassword = MutableLiveData<String>()
    private val _newPasswordRepeat = MutableLiveData<String>()

    val oldPassword: MutableLiveData<String> get() = _oldPassword
    val newPassword: MutableLiveData<String> get() = _newPassword
    val newPasswordRepeat: MutableLiveData<String> get() = _newPasswordRepeat
}