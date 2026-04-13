package com.example.exercise_2_music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {
    private val _selectedSong = MutableLiveData<String>()
    val selectedSong: LiveData<String> get() = _selectedSong

    private var songList: List<String> = emptyList()
    private var currentIndex: Int = -1

    fun setListAndSelect(list: List<String>, index: Int) {
        songList = list
        currentIndex = index
        _selectedSong.value = songList[currentIndex]
    }

    fun nextSong() {
        if (songList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % songList.size
            _selectedSong.value = songList[currentIndex]
        }
    }

    fun previousSong() {
        if (songList.isNotEmpty()) {
            currentIndex = if (currentIndex > 0) currentIndex - 1 else songList.size - 1
            _selectedSong.value = songList[currentIndex]
        }
    }
}