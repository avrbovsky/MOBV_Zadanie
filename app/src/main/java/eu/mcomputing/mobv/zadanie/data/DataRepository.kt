package eu.mcomputing.mobv.zadanie.data

import android.content.Context
import android.util.Log
import eu.mcomputing.mobv.zadanie.data.api.ApiService
import eu.mcomputing.mobv.zadanie.data.api.model.GeofenceUpdateRequest
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordChangeRequest
import eu.mcomputing.mobv.zadanie.data.api.model.PasswordResetRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserLoginRequest
import eu.mcomputing.mobv.zadanie.data.api.model.UserRegistrationRequest
import eu.mcomputing.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputing.mobv.zadanie.data.db.LocalCache
import eu.mcomputing.mobv.zadanie.data.db.entities.GeofenceEntity
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                            ApiService.create(context),
                LocalCache(AppRoomDatabase.getInstance(context).appDao())
                ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(
        username: String,
        email: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty", null)
        }
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try {
            val response = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    Log.d("Response", json_response.uid)
                    if(json_response.uid == "-1" || json_response.uid == "-2"){
                        return Pair("Failed to create user, username or email already in use.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            email,
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to create user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to create user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to create user.", null)
    }

    suspend fun apiResetPassword(email: String): Pair<String, Boolean> {
        if (email.isEmpty()){
            return Pair("Email can not be empty.", false)
        }
        try {
            val response = service.resetPassword(PasswordResetRequest(email))
            if(response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.status == "failure") {
                        return Pair(json_response.message, false)
                    }
                    return Pair("", true)
                }
            }
            return Pair("Failed to request reset email.", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to request reset email.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to request reset email.", false)
    }

    suspend fun apiLoginUser(
        username: String,
        password: String
    ): Pair<String, User?> {
        if (username.isEmpty()) {
            return Pair("Username can not be empty.", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty.", null)
        }
        try {
            val response = service.loginUser(UserLoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.uid == "-1") {
                        return Pair("Wrong password or username.", null)
                    }
                    return Pair(
                        "",
                        User(
                            username,
                            "",
                            json_response.uid,
                            json_response.access,
                            json_response.refresh
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiChangePassword(oldPassword: String, newPassword: String): Pair<String, Boolean> {
        try {
            val response = service.changePassword(PasswordChangeRequest(oldPassword, newPassword))
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.status == "success") {
                        return Pair("Password changed successfully", true)
                    }
                    return Pair(
                        "",
                        false
                    )
                }
            }
            return Pair("Failed to change password", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to change password.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to change password.", false)
    }

    suspend fun apiGetUser(
        uid: String
    ): Pair<String, User?> {
        try {
            val response = service.getUser(uid)

            if (response.isSuccessful) {
                response.body()?.let {
                    return Pair("", User(it.name, "", it.id, "", "", it.photo))
                }
            }

            return Pair("Failed to load user", null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to load user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load user.", null)
    }

    suspend fun apiUploadPicture(file: File?): Pair<String, Boolean> {
        if (file == null) {
            return Pair("File can not be empty", false)
        }

        try {
            val image = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())

            val response = service.uploadProfilePicture("https://upload.mcomputing.eu/user/photo.php", image)
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    if (json_response.id.isNotEmpty()) {
                        return Pair("Profile picture changed successfully.", true)
                    }
                    return Pair(
                        json_response.status,
                        false
                    )
                }
            }
            return Pair("Failed to change profile picture.", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to change profile picture.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to change profile picture.", false)
    }

    suspend fun apiDeletePicture(): Pair<String, Boolean> {
        try {
            val response = service.deleteProfilePicture("https://upload.mcomputing.eu/user/photo.php")
            if (response.isSuccessful) {
                response.body()?.let { json_response ->
                    return Pair("Profile picture changed successfully.", true)
                }
            }
            return Pair("Failed to change profile picture.", false)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to change profile picture.", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to change profile picture.", false)
    }

    suspend fun apiListGeofence(): String {
        try {
            val response = service.listGeofence()

            if (response.isSuccessful) {
                response.body()?.list?.let {
                    val users = it.map {
                        UserEntity(
                            it.uid, it.name, it.updated,
                            0.0, 0.0, it.radius, it.photo
                        )
                    }
                    cache.insertUserItems(users)
                    return ""
                }
                response.body()?.me?.let {

                }
            }

            return "Failed to load users"
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to load users."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to load users."
    }

    fun getUsers() = cache.getUsers()

    suspend fun getUsersList() = cache.getUsersList()

    suspend fun insertGeofence(item: GeofenceEntity) {
        cache.insertGeofence(item)
        try {
            val response =
                service.updateGeofence(GeofenceUpdateRequest(item.lat, item.lon, item.radius))

            if (response.isSuccessful) {
                response.body()?.let {

                    item.uploaded = true
                    cache.insertGeofence(item)
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getGeofences() = cache.getGeofences()

    suspend fun getUserFromCache(id: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            cache.getUserItem(id)
        }
    }

    suspend fun removeGeofence() {
        try {
            val response = service.deleteGeofence()

            if (response.isSuccessful) {
                response.body()?.let {
                    return
                }
            }

            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

