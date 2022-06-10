package ru.nikitazar.netology_diploma.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.repository.PostRepository
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PostRepository,
    private val auth: AppAuth,
    private val calendar: Calendar
) : ViewModel() {

    private val noPhoto = PhotoModel()

    private val _avatar = MutableLiveData(noPhoto)
    val avatar: LiveData<PhotoModel>
        get() = _avatar

    fun updateUser(login: String, pass: String) = viewModelScope.launch {
        try {
            val authState = repository.updateUser(login, pass)
            auth.setAuth(authState.id, authState.token, login)
        } catch (e: Exception) {
            Log.i("updateUser", e.message.toString())
        }
    }

    fun registerUser(login: String, pass: String, name: String) = viewModelScope.launch {
        try {
            when (_avatar.value) {
                noPhoto -> {
                    val authState = repository.registerUser(login, pass, name)
                    auth.setAuth(authState.id, authState.token, login)
                }
                else -> {
                    _avatar.value?.file?.let { file ->
                        val authState = repository.registerWithPhoto(login, pass, name, MediaUpload(file))
                        auth.setAuth(authState.id, authState.token, login)
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("updateUser", e.message.toString())
        }
    }

    fun changeAvatar(uri: Uri?) {
        _avatar.value = PhotoModel(uri, uri?.toFile())
    }
}