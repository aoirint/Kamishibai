package com.aoirint.kamishibai.musicregistry

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MusicViewModel(
    private val repository: MusicRepository
) : ViewModel() {
    val allMusics: LiveData<List<Music>> = repository.allMusics.asLiveData()

    fun insert(music: Music, callback: (success: Boolean) -> Unit) = viewModelScope.launch {
        try {
            repository.insert(music)
            callback(true)
        } catch (error: SQLiteConstraintException) {
            callback(false)
        }

    }
}

class MusicViewModelFactory(
    private val repository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
