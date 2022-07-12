package ru.nikitazar.netology_diploma.viewModel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
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
import ru.nikitazar.netology_diploma.dto.*
import ru.nikitazar.netology_diploma.model.ActionType
import ru.nikitazar.netology_diploma.model.FeedModelState
import ru.nikitazar.netology_diploma.model.PhotoModel
import ru.nikitazar.netology_diploma.repository.eventRepository.EventRepository
import ru.nikitazar.netology_diploma.utils.SingleLiveEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val auth: AppAuth,
    private val calendar: Calendar,
) : ViewModel() {

   private val empty = Event(
        id = 0,
        authorId = 0,
        author = "",
        authorAvatar = "",
        content = "",
        datetime = "",
        published = "",
        coords = null,
        type = EventType.OFFLINE,
        likeOwnerIds = emptyList(),
        likedByMe = false,
        speakerIds = emptyList(),
        participantsIds = emptyList(),
        participatedByMe = false,
        attachment = null,
        link = null,
    )

    private val cached
        get() = repository.data.cachedIn(viewModelScope)

    @SuppressLint("SimpleDateFormat")
    val data: Flow<PagingData<Event>> = auth.authStateFlow
        .flatMapLatest {
            cached.map { pagingData ->
                pagingData.map { event ->
                    event.copy(
                        published = convertTimeFormat(event.published),
                        datetime = convertTimeFormat(event.datetime)
                    )
                }
            }
        }

    val eventById: LiveData<Event>
        get() = _eventById
    private val _eventById = MutableLiveData(Event())

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
    private val _dataState = MutableLiveData(FeedModelState())

    val edited = MutableLiveData(empty)

    val eventCreated: LiveData<Unit>
        get() = _eventCreated
    private val _eventCreated = SingleLiveEvent<Unit>()

    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)

    val photo: LiveData<PhotoModel>
        get() = _photo


    fun cancelEdit() = viewModelScope.launch {
        edited.value = empty
    }

    fun save(event: Event) = viewModelScope.launch {
        try {
            changeContent(event)
            edited.value?.let { event1 ->
                when (_photo.value) {
                    noPhoto -> repository.save(event1, false)
                    else -> _photo.value?.file?.let { file ->
                        repository.saveWithAttachment(event1, MediaUpload(file), false)
                    }
                }
            }
            _eventCreated.postValue(Unit)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = false, actionType = ActionType.NULL)
        } finally {
            edited.value = empty
            _photo.value = noPhoto
        }
    }

    fun edit(event: Event) = viewModelScope.launch {
        edited.value = event
    }

    private fun changeContent(event: Event) {
        if (edited.value == event) {
            return
        }
        edited.value = event.copy(published = calendar.time.time.toString())
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

    fun joinById(id: Long) = viewModelScope.launch {
        try {
            repository.joinById(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, actionType = ActionType.REMOVE, actionId = id)
        }
    }

    fun rejectById(id: Long) = viewModelScope.launch {
        try {
            repository.rejectById(id)
        } catch (e: Exception) {
            _dataState.value =
                FeedModelState(error = true, actionType = ActionType.REMOVE, actionId = id)
        }
    }


    fun getById(id: Long) = viewModelScope.launch {
        _eventById.postValue(repository.getById(id))
    }
}