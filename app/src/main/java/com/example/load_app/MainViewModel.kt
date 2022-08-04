package com.example.load_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val processState: MutableLiveData<ButtonState> = MutableLiveData(ButtonState.Completed)

    init {

    }

    fun update() {
        viewModelScope.launch {

            processState.value = ButtonState.Loading
            delay(1000L)
            processState.value = ButtonState.Completed

        }
    }
}