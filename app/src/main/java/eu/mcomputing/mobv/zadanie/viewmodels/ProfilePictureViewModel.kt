package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import kotlinx.coroutines.launch
import java.io.File

class ProfilePictureViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _messageResult = MutableLiveData<String>()

    val messageResult: LiveData<String> get() = _messageResult

    private val _statusResult = MutableLiveData<Boolean>()

    val statusResult: LiveData<Boolean> get() = _statusResult

    fun deletePicture() {
        viewModelScope.launch {
            val result = dataRepository.apiDeletePicture()
            _messageResult.postValue(result.first ?: "")
            _statusResult.postValue(result.second ?: false)
        }
    }

    fun uploadPicture(file: File?) {
        viewModelScope.launch {
            val result = dataRepository.apiUploadPicture(file)
            _messageResult.postValue(result.first ?: "")
            _statusResult.postValue(result.second ?: false)
        }
    }

    fun resetState() {
        _messageResult.postValue("")
        _statusResult.postValue(false)
    }
}