package ru.nikitazar.netology_diploma.viewModel

import android.annotation.SuppressLint
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.nikitazar.netology_diploma.auth.AppAuth
import ru.nikitazar.netology_diploma.dto.Coords
import ru.nikitazar.netology_diploma.dto.Event
import ru.nikitazar.netology_diploma.dto.MediaUpload
import ru.nikitazar.netology_diploma.dto.Post
import ru.nikitazar.netology_diploma.errors.DbError
import ru.nikitazar.netology_diploma.model.ActionType
import ru.nikitazar.netology_diploma.model.FeedModelState
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.repository.postRepository.PostRepository
import ru.nikitazar.netology_diploma.utils.SingleLiveEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    coords = Coords(0F, 0F),
    link = null,
    mentionIds = emptyList(),
    mentionedMe = false,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    attachment = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val auth: AppAuth,
    private val calendar: Calendar,
) : ViewModel() {

    private val _dataState = MutableLiveData(FeedModelState())

    private val cached
        get() = repository.data.cachedIn(viewModelScope)

    @SuppressLint("SimpleDateFormat")
    val data: Flow<PagingData<Post>> = auth.authStateFlow
        .flatMapLatest {
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(published = convertTimeFormat(post.published))
                }
            }
        }

    val postById: LiveData<Post>
        get() = _postById
    private val _postById = MutableLiveData(Post())

    private fun convertTimeFormat(ts: String): String {
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .parse(ts.replace("T", " ").replace("Z", ""))
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        } catch (e: Exception) {
            Log.e("convertTimeFormat", ts)
            ts
        }
    }

    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()

    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)

    val photo: LiveData<PhotoModel>
        get() = _photo


    fun cancelEdit() = viewModelScope.launch {
        edited.value = empty
    }

    fun save(content: String) = viewModelScope.launch {
        try {
            changeContent(content)
            edited.value?.let { post ->
                when (_photo.value) {
                    noPhoto -> repository.save(post, false)
                    else -> _photo.value?.file?.let { file ->
                        repository.saveWithAttachment(post, MediaUpload(file), false)
                    }
                }
            }
            _postCreated.postValue(Unit)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = false, actionType = ActionType.NULL)
        } finally {
            edited.value = empty
            _photo.value = noPhoto
        }
    }

    fun edit(post: Post) = viewModelScope.launch {
        edited.value = post
    }

    private fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text, published = calendar.time.time.toString())
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, actionType = ActionType.LIKE, actionId = id)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            repository.dislikeById(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, actionType = ActionType.DISLIKE, actionId = id)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, actionType = ActionType.REMOVE, actionId = id)
        }
    }

    fun changePhoto(uri: Uri?, file: File?) = viewModelScope.launch {
        _photo.value = PhotoModel(uri, file)
    }

    fun changeCoords(coords: Coords) = viewModelScope.launch {
        edited.value = edited.value?.copy(coords = coords)
    }

    fun getById(id: Long) = viewModelScope.launch {
        try {
            val post = repository.getPostById(id)
            _postById.postValue(post.copy(published = convertTimeFormat(post.published)))
        } catch (e: DbError) {
            Log.e("getById", id.toString())
        }
    }
}