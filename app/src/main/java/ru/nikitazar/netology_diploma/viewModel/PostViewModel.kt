package ru.nikitazar.netology_diploma.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.repository.PostRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PostRepository,
    private val auth: AppAuth,
) : ViewModel() {

    private val cached
        get() = repository.data.cachedIn(viewModelScope)

    @SuppressLint("SimpleDateFormat")
    val data: Flow<PagingData<Post>> = auth.authStateFlow
        .flatMapLatest { cached } //TODO convert time format

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