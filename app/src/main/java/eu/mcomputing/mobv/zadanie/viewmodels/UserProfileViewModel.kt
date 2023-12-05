package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import kotlinx.coroutines.launch

class UserProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _userResult = MutableLiveData<UserEntity?>()
    val userResult: LiveData<UserEntity?> get() = _userResult

    val geofences: LiveData<List<GeofenceEntity>?> = liveData{ emitSource(dataRepository.getGeofences()) }
    fun loadUserById(uid: String) {
        viewModelScope.launch {
            val result = dataRepository.getUserFromCache(uid)
            _userResult.postValue(result)
        }
    }
}