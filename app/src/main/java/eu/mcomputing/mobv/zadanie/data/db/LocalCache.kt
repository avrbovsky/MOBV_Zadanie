package eu.mcomputing.mobv.zadanie.data.db

import androidx.lifecycle.LiveData
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity


class LocalCache(private val dao: DbDao) {

    suspend fun logoutUser() {
        deleteUserItems()
    }

    suspend fun insertUserItems(items: List<UserEntity>) {
        dao.insertUserItems(items)
    }

    fun getUserItem(uid: String): LiveData<UserEntity?> {
        return dao.getUserItem(uid)
    }

    fun getUsers(): LiveData<List<UserEntity>?> = dao.getUsers()

    suspend fun deleteUserItems() {
        dao.deleteUserItems()
    }

}